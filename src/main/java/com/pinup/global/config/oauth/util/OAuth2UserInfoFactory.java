package com.pinup.global.config.oauth.util;

import com.pinup.domain.member.entity.LoginType;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(LoginType loginType, Map<String, Object> attributes) {
        if (loginType == LoginType.NAVER) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (loginType == LoginType.KAKAO) {
            return new KakaoOAuth2UserInfo(attributes);
        }
        return null;
    }
}
