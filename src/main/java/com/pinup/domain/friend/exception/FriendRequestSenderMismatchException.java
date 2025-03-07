package com.pinup.domain.friend.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class FriendRequestSenderMismatchException extends BusinessException {

    public FriendRequestSenderMismatchException() {
        super(ErrorCode.FRIEND_REQUEST_SENDER_MISMATCH);
    }
}
