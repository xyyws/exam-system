package com.exam.auth.service.impl;

import com.exam.auth.dto.request.LoginRequest;
import com.exam.auth.dto.response.LoginResponse;
import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.model.LoginUser;
import com.exam.common.security.JwtTokenProvider;
import com.exam.common.security.RedisTokenStore;
import com.exam.common.security.SecurityUtils;
import com.exam.system.entity.SysRole;
import com.exam.system.entity.SysUser;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysRoleMapper;
import com.exam.system.mapper.SysUserMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SysUserMapper userMapper;
    @Mock
    private SysRoleMapper roleMapper;
    @Mock
    private SysClassMapper classMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTokenStore redisTokenStore;
    @Mock
    private Claims claims;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userMapper, roleMapper, classMapper, passwordEncoder, jwtTokenProvider, redisTokenStore);
    }

    @Test
    void should_return_tokens_and_user_info_when_login_successful() {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("plain");

        SysUser user = buildUser(1L, "alice", "encoded", 1, 1);
        SysRole role = buildRole(10L, "TEACHER");

        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);
        when(roleMapper.selectByUserId(1L)).thenReturn(List.of(role));
        when(jwtTokenProvider.createAccessToken(any(LoginUser.class))).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(1L)).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpire()).thenReturn(7200L);

        LoginResponse response = authService.login(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(7200L, response.getExpiresIn());
        assertNotNull(response.getUser());
        assertEquals("alice", response.getUser().getUsername());
        assertEquals(List.of("TEACHER"), response.getUser().getRoles());
    }

    @Test
    void should_throw_business_exception_when_password_not_match_on_login() {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("bad");

        SysUser user = buildUser(1L, "alice", "encoded", 1, 1);
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("bad", "encoded")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals(ApiCodeEnum.USERNAME_PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    void should_blacklist_token_and_remove_cached_user_when_logout_successful() {
        when(jwtTokenProvider.parseClaims("token-value")).thenReturn(claims);
        when(claims.getId()).thenReturn("jti-1");
        when(claims.getExpiration()).thenReturn(new java.util.Date(System.currentTimeMillis() + 10_000));
        when(claims.getSubject()).thenReturn("1");

        authService.logout("token-value");

        verify(redisTokenStore).addToBlacklist(eq("jti-1"), anyLong());
        verify(redisTokenStore).removeLoginUser(1L);
    }

    @Test
    void should_return_new_tokens_when_refresh_token_valid() {
        SysUser user = buildUser(1L, "alice", "encoded", 1, 1);
        SysRole role = buildRole(10L, "ADMIN");

        when(jwtTokenProvider.parseClaims("refresh-token")).thenReturn(claims);
        when(claims.get("type", String.class)).thenReturn("refresh");
        when(claims.getSubject()).thenReturn("1");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(roleMapper.selectByUserId(1L)).thenReturn(List.of(role));
        when(jwtTokenProvider.createAccessToken(any(LoginUser.class))).thenReturn("new-access");
        when(jwtTokenProvider.createRefreshToken(1L)).thenReturn("new-refresh");
        when(jwtTokenProvider.getAccessTokenExpire()).thenReturn(3600L);

        LoginResponse response = authService.refresh("refresh-token");

        assertEquals("new-access", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void should_throw_token_expired_when_refresh_parse_fails() {
        when(jwtTokenProvider.parseClaims("bad-token")).thenThrow(new RuntimeException("bad token"));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.refresh("bad-token"));

        assertEquals(ApiCodeEnum.TOKEN_EXPIRED.getCode(), exception.getCode());
    }

    @Test
    void should_return_current_user_info_when_user_exists() {
        LoginUser loginUser = new LoginUser(1L, "alice", 1, List.of("TEACHER"));
        SysUser user = buildUser(1L, "alice", "encoded", 1, 1);
        SysRole role = buildRole(10L, "TEACHER");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(roleMapper.selectByUserId(1L)).thenReturn(List.of(role));

        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUser).thenReturn(loginUser);

            LoginResponse.UserInfo userInfo = authService.getCurrentUser();

            assertEquals(1L, userInfo.getId());
            assertEquals("alice", userInfo.getUsername());
            assertEquals(List.of("TEACHER"), userInfo.getRoles());
        }
    }

    @Test
    void should_throw_user_not_found_when_current_user_missing() {
        LoginUser loginUser = new LoginUser(1L, "alice", 1, List.of("TEACHER"));
        when(userMapper.selectById(1L)).thenReturn(null);

        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUser).thenReturn(loginUser);

            BusinessException exception = assertThrows(BusinessException.class, authService::getCurrentUser);

            assertEquals(ApiCodeEnum.USER_NOT_FOUND.getCode(), exception.getCode());
        }
    }

    private SysUser buildUser(Long id, String username, String password, Integer userType, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setUserType(userType);
        user.setStatus(status);
        user.setRealName("Alice");
        user.setCreateTime(LocalDateTime.now());
        return user;
    }

    private SysRole buildRole(Long id, String roleCode) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleCode(roleCode);
        role.setRoleName(roleCode);
        role.setStatus(1);
        return role;
    }
}
