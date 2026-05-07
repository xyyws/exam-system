package com.exam.system.dto.request;

public class UserUpdateRequest {
    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private String deptName;
    private Long classId;
    private String remark;

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
