package com.exam.system.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ClassCreateRequest {
    @NotBlank private String className;
    private String gradeName;
    private String deptName;
    private Long teacherId;
    private Integer status = 1;
    private String remark;

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
