package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;

public class ImagesLimitExceededException extends BusinessException {

    public ImagesLimitExceededException() {
        super(ErrorCode.IMAGES_LIMIT_EXCEEDED);
    }
}
