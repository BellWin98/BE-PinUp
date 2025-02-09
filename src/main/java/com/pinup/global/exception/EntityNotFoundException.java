package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class EntityNotFoundException extends BusinessException{
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
