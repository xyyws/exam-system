package com.exam.common.exception;

import com.exam.common.enums.ApiCodeEnum;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() { super(ApiCodeEnum.FORBIDDEN); }
}
