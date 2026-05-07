package com.exam.common.enums;

public enum RoleCodeEnum {
    ADMIN("ADMIN"),
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    private final String code;

    RoleCodeEnum(String code) { this.code = code; }
    public String getCode() { return code; }
}
