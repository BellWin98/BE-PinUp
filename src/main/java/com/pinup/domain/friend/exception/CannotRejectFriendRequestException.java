package com.pinup.domain.friend.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class CannotRejectFriendRequestException extends BusinessException {

    public CannotRejectFriendRequestException() {
        super(ErrorCode.CANNOT_REJECT_FRIEND_REQUEST);
    }
}
