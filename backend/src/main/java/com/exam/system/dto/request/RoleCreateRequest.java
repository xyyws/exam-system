package com.exam.system.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RoleCreateRequest {
    @NotBlank private String roleCode;
    @NotBlank private String roleName;
    private Integer status = 1;
    private String remark;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
