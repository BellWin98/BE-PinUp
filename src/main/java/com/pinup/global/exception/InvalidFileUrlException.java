package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class InvalidFileUrlException extends BusinessException {

    public InvalidFileUrlException() {
        super(ErrorCode.INVALID_FILE_URL);
    }
}
