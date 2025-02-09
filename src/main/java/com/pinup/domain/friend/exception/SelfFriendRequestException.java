package com.pinup.domain.friend.exception;

import com.pinup.global.exception.BusinessException;
import com.pinup.global.response.ErrorCode;

public class SelfFriendRequestException extends BusinessException {

    public SelfFriendRequestException() {
        super(ErrorCode.SELF_FRIEND_REQUEST);
    }
}
