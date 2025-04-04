package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class FileProcessingException extends BusinessException{
    public FileProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
