package com.pinup.global.config.oauth.service;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.global.config.oauth.util.NaverOAuth2UserInfo;
import com.pinup.global.config.oauth.util.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class NaverOAuth2Service extends AbstractOAuth2Service{

    @Value("${oauth2.provider.naver.client-id}")
    private String clientId;

    @Value("${oauth2.provider.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth2.provider.naver.token-uri}")
    private String tokenUri;

    @Value("${oauth2.provider.naver.user-info-uri}")
    private String userInfoUri;

    public NaverOAuth2Service(WebClient webClient) {
        super(webClient);
    }

    @Override
    protected String getTokenUrl() {
        return tokenUri;
    }

    @Override
    protected String getClientId() {
        return clientId;
    }

    @Override
    protected String getClientSecret() {
        return clientSecret;
    }

    @Override
    protected String getUserInfoUrl() {
        return userInfoUri;
    }

    @Override
    protected OAuth2UserInfo extractUserInfo(Map<String, Object> attributes) {
        return new NaverOAuth2UserInfo(attributes);
    }

    @Override
    public String getProvider() {
        return "naver";
    }
}
