package com.exam.common.enums;

public enum UserTypeEnum {
    ADMIN(1, "管理员"),
    TEACHER(2, "教师"),
    STUDENT(3, "学生");

    private final Integer code;
    private final String desc;

    UserTypeEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
