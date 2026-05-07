package com.exam.system.controller;

import com.exam.common.web.ApiResponse;
import com.exam.system.dto.request.RoleCreateRequest;
import com.exam.system.dto.response.RoleResponse;
import com.exam.system.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) { this.roleService = roleService; }

    @GetMapping
    public ApiResponse<List<RoleResponse>> list() {
        return ApiResponse.success(roleService.listRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> get(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRole(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody RoleCreateRequest request) {
        return ApiResponse.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody RoleCreateRequest request) {
        roleService.updateRole(id, request);
        return ApiResponse.success();
    }
}
