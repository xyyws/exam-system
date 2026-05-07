package com.exam.system.service.impl;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.enums.UserTypeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.PageResponse;
import com.exam.system.dto.request.UserCreateRequest;
import com.exam.system.dto.request.UserQueryRequest;
import com.exam.system.dto.request.UserUpdateRequest;
import com.exam.system.dto.response.UserResponse;
import com.exam.system.entity.SysClass;
import com.exam.system.entity.SysRole;
import com.exam.system.entity.SysUser;
import com.exam.system.entity.SysUserRole;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysRoleMapper;
import com.exam.system.mapper.SysUserMapper;
import com.exam.system.mapper.SysUserRoleMapper;
import com.exam.system.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysClassMapper classMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper userMapper, SysRoleMapper roleMapper,
                           SysUserRoleMapper userRoleMapper, SysClassMapper classMapper,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.classMapper = classMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResponse<UserResponse> listUsers(UserQueryRequest req) {
        int offset = (req.getPageNum() - 1) * req.getPageSize();
        List<SysUser> users = userMapper.selectList(req.getKeyword(), req.getUserType(),
                req.getStatus(), req.getClassId(), offset, req.getPageSize());
        long total = userMapper.countList(req.getKeyword(), req.getUserType(), req.getStatus(), req.getClassId());

        List<UserResponse> records = users.stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(total, req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public UserResponse getUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        return toResponse(user);
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest req) {
        SysUser exist = userMapper.selectByUsername(req.getUsername());
        if (exist != null) throw new BusinessException("B004", "用户名已存在");

        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setGender(req.getGender());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setUserType(req.getUserType());
        user.setStudentNo(req.getStudentNo());
        user.setTeacherNo(req.getTeacherNo());
        user.setDeptName(req.getDeptName());
        user.setClassId(req.getClassId());
        user.setStatus(1);
        user.setCreateBy(SecurityUtils.getCurrentUserId());
        userMapper.insert(user);

        // 如果没有指定角色，根据 userType 自动分配对应角色
        List<Long> roleIds = req.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            String roleCode;
            if (req.getUserType() == 1) {
                roleCode = "ADMIN";
            } else if (req.getUserType() == 2) {
                roleCode = "TEACHER";
            } else {
                roleCode = "STUDENT";
            }
            SysRole role = roleMapper.selectByCode(roleCode);
            if (role != null) {
                roleIds = java.util.List.of(role.getId());
            }
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream().map(rid -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(rid);
                return ur;
            }).collect(Collectors.toList());
            userRoleMapper.insertBatch(userRoles);
        }
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest req) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        user.setRealName(req.getRealName());
        user.setGender(req.getGender());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setDeptName(req.getDeptName());
        user.setClassId(req.getClassId());
        user.setUpdateBy(SecurityUtils.getCurrentUserId());
        userMapper.update(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        userMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void assignRoles(Long id, List<Long> roleIds) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        userRoleMapper.deleteByUserId(id);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream().map(rid -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(id);
                ur.setRoleId(rid);
                return ur;
            }).collect(Collectors.toList());
            userRoleMapper.insertBatch(userRoles);
        }
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(ApiCodeEnum.USER_NOT_FOUND);
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 先删除用户角色关联
        userMapper.deleteUserRoleByUserId(id);
        // 再物理删除用户
        userMapper.physicalDelete(id);
    }

    private UserResponse toResponse(SysUser u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setRealName(u.getRealName());
        r.setGender(u.getGender());
        r.setPhone(u.getPhone());
        r.setEmail(u.getEmail());
        r.setAvatar(u.getAvatar());
        r.setUserType(u.getUserType());
        r.setStudentNo(u.getStudentNo());
        r.setTeacherNo(u.getTeacherNo());
        r.setDeptName(u.getDeptName());
        r.setClassId(u.getClassId());
        r.setStatus(u.getStatus());
        r.setRemark(u.getRemark());
        r.setCreateTime(u.getCreateTime());

        if (u.getClassId() != null) {
            SysClass cls = classMapper.selectById(u.getClassId());
            if (cls != null) r.setClassName(cls.getClassName());
        }
        List<SysRole> roles = roleMapper.selectByUserId(u.getId());
        r.setRoles(roles.stream().map(rl -> {
            UserResponse.RoleSimple rs = new UserResponse.RoleSimple();
            rs.setId(rl.getId());
            rs.setRoleCode(rl.getRoleCode());
            rs.setRoleName(rl.getRoleName());
            return rs;
        }).collect(Collectors.toList()));
        return r;
    }
}
