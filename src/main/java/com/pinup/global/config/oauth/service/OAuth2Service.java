package com.pinup.global.config.oauth.service;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.global.config.oauth.util.OAuth2UserInfo;

public interface OAuth2Service {

    OAuth2UserInfo getUserInfo(String code);
    String getProvider();
}
