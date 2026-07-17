package com.exam.auth.service.impl;

import com.exam.auth.dto.request.LoginRequest;
import com.exam.auth.dto.response.LoginResponse;
import com.exam.auth.service.AuthService;
import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.enums.UserStatusEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.model.LoginUser;
import com.exam.common.security.JwtTokenProvider;
import com.exam.common.security.RedisTokenStore;
import com.exam.common.security.SecurityUtils;
import com.exam.system.entity.SysClass;
import com.exam.system.entity.SysRole;
import com.exam.system.entity.SysUser;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysRoleMapper;
import com.exam.system.mapper.SysUserMapper;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysClassMapper classMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenStore redisTokenStore;

    public AuthServiceImpl(SysUserMapper userMapper, SysRoleMapper roleMapper, SysClassMapper classMapper,
                           PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                           RedisTokenStore redisTokenStore) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.classMapper = classMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTokenStore = redisTokenStore;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ApiCodeEnum.USERNAME_PASSWORD_ERROR);
        }
        if (UserStatusEnum.DISABLED.getCode().equals(user.getStatus())) {
            throw new BusinessException(ApiCodeEnum.USER_DISABLED);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ApiCodeEnum.USERNAME_PASSWORD_ERROR);
        }

        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getUserType(), roleCodes);
        loginUser.setRealName(user.getRealName());
        String accessToken = jwtTokenProvider.createAccessToken(loginUser);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setUserType(user.getUserType());
        userInfo.setRoles(roleCodes);
        if (user.getClassId() != null) {
            SysClass cls = classMapper.selectById(user.getClassId());
            if (cls != null) userInfo.setClassName(cls.getClassName());
        }
        response.setUser(userInfo);

        log.info("用户登录成功: username={}", user.getUsername());
        return response;
    }

    @Override
    @Transactional
    public void logout(String token) {
        try {
            Claims claims = jwtTokenProvider.parseClaims(token);
            redisTokenStore.addToBlacklist(claims.getId(), claims.getExpiration().getTime());
            redisTokenStore.removeLoginUser(Long.valueOf(claims.getSubject()));
        } catch (Exception e) {
            log.warn("登出token处理异常", e);
        }
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        try {
            Claims claims = jwtTokenProvider.parseClaims(refreshToken);
            if (!"refresh".equals(claims.get("type", String.class))) {
                throw new BusinessException(ApiCodeEnum.UNAUTHORIZED);
            }
            Long userId = Long.valueOf(claims.getSubject());
            SysUser user = userMapper.selectById(userId);
            if (user == null || UserStatusEnum.DISABLED.getCode().equals(user.getStatus())) {
                throw new BusinessException(ApiCodeEnum.USER_DISABLED);
            }
            List<SysRole> roles = roleMapper.selectByUserId(user.getId());
            List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

            LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getUserType(), roleCodes);
            loginUser.setRealName(user.getRealName());
            String newAccessToken = jwtTokenProvider.createAccessToken(loginUser);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());

            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ApiCodeEnum.TOKEN_EXPIRED);
        }
    }

    @Override
    public LoginResponse.UserInfo getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = userMapper.selectById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        }
        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setUserType(user.getUserType());
        userInfo.setRoles(roleCodes);
        if (user.getClassId() != null) {
            SysClass cls = classMapper.selectById(user.getClassId());
            if (cls != null) userInfo.setClassName(cls.getClassName());
        }
        return userInfo;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ApiCodeEnum.OLD_PASSWORD_ERROR);
        }
        String encoded = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, encoded);
        log.info("用户修改密码成功: userId={}", userId);
    }
}
