package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class EntityAlreadyExistException extends BusinessException{
    public EntityAlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
