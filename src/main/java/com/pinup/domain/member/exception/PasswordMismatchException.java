package com.pinup.domain.member.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class PasswordMismatchException extends BusinessException {

    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }
}
