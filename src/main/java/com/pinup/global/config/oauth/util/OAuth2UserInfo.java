package com.pinup.global.config.oauth.util;

import com.pinup.domain.member.entity.LoginType;

import java.util.Map;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
    String getProfileImageUrl();
    Map<String, Object> getAttributes();
}
