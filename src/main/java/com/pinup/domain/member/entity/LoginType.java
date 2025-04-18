package com.pinup.domain.member.entity;

import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;

public enum LoginType {
    SELF, KAKAO, GOOGLE, NAVER

    ;

    public static LoginType getLoginType(String loginType) {
        for (LoginType type : LoginType.values()) {
            if (type.name().equalsIgnoreCase(loginType)) {
                return type;
            }
        }
        throw new EntityNotFoundException(ErrorCode.LOGIN_TYPE_NOT_FOUND);
    }
}
