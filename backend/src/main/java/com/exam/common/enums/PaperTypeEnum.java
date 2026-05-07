package com.exam.common.enums;

public enum PaperTypeEnum {
    MANUAL(1, "手动组卷"),
    AUTO(2, "自动组卷");

    private final Integer code;
    private final String desc;

    PaperTypeEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
}
