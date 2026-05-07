package com.exam.common.enums;

public enum DifficultyLevelEnum {
    EASY(1, "简单"),
    RELATIVELY_EASY(2, "较易"),
    MEDIUM(3, "中等"),
    RELATIVELY_HARD(4, "较难"),
    HARD(5, "困难");

    private final Integer code;
    private final String desc;

    DifficultyLevelEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
