package com.exam.common.enums;

public enum UserStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    UserStatusEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
}
