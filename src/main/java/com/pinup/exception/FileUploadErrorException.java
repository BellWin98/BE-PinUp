package com.pinup.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.exception.ErrorCode;

public class FileUploadErrorException extends BusinessException {

    public FileUploadErrorException() {
        super(ErrorCode.FILE_EXTENSION_INVALID);
    }
}
