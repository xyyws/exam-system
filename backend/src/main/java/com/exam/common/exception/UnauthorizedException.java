package com.exam.common.exception;

import com.exam.common.enums.ApiCodeEnum;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() { super(ApiCodeEnum.UNAUTHORIZED); }
    public UnauthorizedException(String message) { super(ApiCodeEnum.UNAUTHORIZED.getCode(), message); }
}
