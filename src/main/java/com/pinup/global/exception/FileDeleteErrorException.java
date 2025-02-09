package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class FileDeleteErrorException extends BusinessException {

    public FileDeleteErrorException() {
        super(ErrorCode.FILE_DELETE_ERROR);
    }
}
