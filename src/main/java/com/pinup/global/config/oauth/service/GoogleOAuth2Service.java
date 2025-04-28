package com.pinup.global.config.oauth.service;

import com.pinup.global.config.oauth.util.GoogleOAuth2UserInfo;
import com.pinup.global.config.oauth.util.OAuth2UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GoogleOAuth2Service extends AbstractOAuth2Service{

    @Value("${oauth2.provider.google.client-id}")
    private String clientId;

    @Value("${oauth2.provider.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.provider.google.token-uri}")
    private String tokenUri;

    @Value("${oauth2.provider.google.user-info-uri}")
    private String userInfoUri;

    public GoogleOAuth2Service(WebClient webClient) {
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
        return new GoogleOAuth2UserInfo(attributes);
    }

    @Override
    public String getProvider() {
        return "google";
    }
}
