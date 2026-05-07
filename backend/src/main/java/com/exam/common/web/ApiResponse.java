package com.exam.common.web;

import com.exam.common.enums.ApiCodeEnum;

public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private String traceId;
    private String timestamp;

    public ApiResponse() {}

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>(ApiCodeEnum.SUCCESS.getCode(), ApiCodeEnum.SUCCESS.getMessage(), data);
        return r;
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static ApiResponse<Void> fail(ApiCodeEnum codeEnum) {
        return fail(codeEnum.getCode(), codeEnum.getMessage());
    }

    public static ApiResponse<Void> fail(String message) {
        return fail(ApiCodeEnum.SYSTEM_ERROR.getCode(), message);
    }
}
