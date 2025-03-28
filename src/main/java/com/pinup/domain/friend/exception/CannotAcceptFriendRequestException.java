package com.pinup.domain.friend.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class CannotAcceptFriendRequestException extends BusinessException {

    public CannotAcceptFriendRequestException() {
        super(ErrorCode.CANNOT_ACCEPT_FRIEND_REQUEST);
    }
}
