package com.exam.common.enums;

public enum QuestionTypeEnum {
    SINGLE_CHOICE(1, "单选题"),
    MULTI_CHOICE(2, "多选题"),
    JUDGMENT(3, "判断题"),
    FILL_BLANK(4, "填空题"),
    SHORT_ANSWER(5, "简答题");

    private final Integer code;
    private final String desc;

    QuestionTypeEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
    public String getDesc() { return desc; }

    public static boolean isObjective(Integer code) {
        return code != null && code >= 1 && code <= 4;
    }

    public static boolean isSubjective(Integer code) {
        return code != null && code == 5;
    }
}
