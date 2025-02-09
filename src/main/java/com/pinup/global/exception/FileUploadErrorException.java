package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class FileUploadErrorException extends BusinessException {

    public FileUploadErrorException() {
        super(ErrorCode.FILE_EXTENSION_INVALID);
    }
}
