package com.exam.system.service.impl;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.system.dto.request.RoleCreateRequest;
import com.exam.system.dto.response.RoleResponse;
import com.exam.system.entity.SysRole;
import com.exam.system.mapper.SysRoleMapper;
import com.exam.system.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;

    public RoleServiceImpl(SysRoleMapper roleMapper) { this.roleMapper = roleMapper; }

    @Override
    public List<RoleResponse> listRoles() {
        return roleMapper.selectAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRole(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException("B102", "角色不存在");
        return toResponse(role);
    }

    @Override
    public Long createRole(RoleCreateRequest req) {
        SysRole role = new SysRole();
        role.setRoleCode(req.getRoleCode());
        role.setRoleName(req.getRoleName());
        role.setStatus(req.getStatus());
        role.setRemark(req.getRemark());
        roleMapper.insert(role);
        return role.getId();
    }

    @Override
    public void updateRole(Long id, RoleCreateRequest req) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException("B102", "角色不存在");
        role.setRoleName(req.getRoleName());
        role.setStatus(req.getStatus());
        role.setRemark(req.getRemark());
        roleMapper.update(role);
    }

    private RoleResponse toResponse(SysRole r) {
        RoleResponse res = new RoleResponse();
        res.setId(r.getId());
        res.setRoleCode(r.getRoleCode());
        res.setRoleName(r.getRoleName());
        res.setStatus(r.getStatus());
        res.setRemark(r.getRemark());
        res.setCreateTime(r.getCreateTime());
        return res;
    }
}
