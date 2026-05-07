package com.exam.common.enums;

public enum ExamPublishStatusEnum {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    FINISHED(2, "已结束"),
    ARCHIVED(3, "已归档");

    private final Integer code;
    private final String desc;

    ExamPublishStatusEnum(Integer code, String desc) { this.code = code; this.desc = desc; }
    public Integer getCode() { return code; }
}
