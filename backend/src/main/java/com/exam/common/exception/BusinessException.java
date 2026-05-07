package com.exam.common.exception;

import com.exam.common.enums.ApiCodeEnum;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ApiCodeEnum codeEnum) {
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }

    public BusinessException(String message) {
        super(message);
        this.code = ApiCodeEnum.SYSTEM_ERROR.getCode();
    }

    public String getCode() { return code; }
}
