package com.pinup.global.config.oauth.util;

import com.pinup.domain.member.entity.LoginType;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;
    private final Map<String, Object> properties;
    private final Map<String, Object> kakaoAccount;

    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.properties = (Map<String, Object>) attributes.get("properties");
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public LoginType getProvider() {
        return LoginType.KAKAO;
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        return (String) properties.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) properties.get("profile_image");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
