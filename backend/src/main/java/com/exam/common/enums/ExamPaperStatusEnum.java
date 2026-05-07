package com.exam.common.enums;

public enum ExamPaperStatusEnum {
    NOT_STARTED(0, "未开始"),
    IN_PROGRESS(1, "进行中"),
    SUBMITTED(2, "已交卷"),
    SCORED(3, "已判分");

    private final Integer code;
    private final String desc;

    ExamPaperStatusEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
}
