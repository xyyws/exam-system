package com.exam.common.enums;

public enum ApiCodeEnum {
    SUCCESS("0", "success"),
    BAD_REQUEST("A001", "参数校验失败"),
    UNAUTHORIZED("A002", "未登录或token无效"),
    TOKEN_EXPIRED("A003", "token已过期"),
    FORBIDDEN("A004", "无权限访问"),
    USER_NOT_FOUND("B001", "用户不存在"),
    USERNAME_PASSWORD_ERROR("B002", "用户名或密码错误"),
    USER_DISABLED("B003", "用户已禁用"),
    OLD_PASSWORD_ERROR("B004", "原密码错误"),
    CLASS_NOT_FOUND("B101", "班级不存在"),
    QUESTION_NOT_FOUND("B201", "题目不存在"),
    QUESTION_TYPE_MISMATCH("B202", "题型与选项不匹配"),
    PAPER_NOT_FOUND("B301", "试卷不存在"),
    PAPER_RULE_INSUFFICIENT("B302", "组卷规则库存不足"),
    EXAM_NOT_FOUND("B401", "考试不存在"),
    EXAM_NOT_PUBLISHED("B402", "考试未发布"),
    EXAM_TIME_INVALID("B403", "不在考试时间范围内"),
    EXAM_STUDENT_NOT_ASSIGNED("B404", "学生未分配到该考试"),
    EXAM_ALREADY_SUBMITTED("B405", "已交卷不可重复提交"),
    EXAM_FINISHED("B406", "考试已结束"),
    MARKING_TASK_NOT_FOUND("B501", "阅卷任务不存在"),
    ANALYTICS_NOT_FOUND("B601", "统计数据不存在"),
    SYSTEM_ERROR("S999", "系统异常");

    private final String code;
    private final String message;

    ApiCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
