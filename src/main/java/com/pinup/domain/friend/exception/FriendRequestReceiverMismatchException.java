package com.pinup.domain.friend.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class FriendRequestReceiverMismatchException extends BusinessException {

    public FriendRequestReceiverMismatchException() {
        super(ErrorCode.FRIEND_REQUEST_RECEIVER_MISMATCH);
    }
}
