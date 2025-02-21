package com.pinup.domain.member.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class ExpiredTokenException extends BusinessException {

    public ExpiredTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }
}
