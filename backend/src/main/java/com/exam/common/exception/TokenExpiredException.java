package com.exam.common.exception;

import com.exam.common.enums.ApiCodeEnum;

public class TokenExpiredException extends BusinessException {
    public TokenExpiredException() { super(ApiCodeEnum.TOKEN_EXPIRED); }
}
