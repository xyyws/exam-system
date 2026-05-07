package com.exam.system.service;

import com.exam.system.dto.request.RoleCreateRequest;
import com.exam.system.dto.response.RoleResponse;
import java.util.List;

public interface RoleService {
    List<RoleResponse> listRoles();
    RoleResponse getRole(Long id);
    Long createRole(RoleCreateRequest request);
    void updateRole(Long id, RoleCreateRequest request);
}
