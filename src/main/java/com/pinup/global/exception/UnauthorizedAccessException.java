package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class UnauthorizedAccessException extends BusinessException{
    public UnauthorizedAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
