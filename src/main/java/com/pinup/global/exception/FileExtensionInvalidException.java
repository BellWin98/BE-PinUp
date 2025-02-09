package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class FileExtensionInvalidException extends BusinessException {

    public FileExtensionInvalidException() {
        super(ErrorCode.FILE_EXTENSION_INVALID);
    }
}
