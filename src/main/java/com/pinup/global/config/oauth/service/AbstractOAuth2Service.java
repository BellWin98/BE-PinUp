package com.pinup.global.config.oauth.service;

import com.pinup.global.config.oauth.util.OAuth2UserInfo;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractOAuth2Service implements OAuth2Service {

    private final WebClient webClient;

    protected abstract String getTokenUrl();
    protected abstract String getClientId();
    protected abstract String getClientSecret();
    protected abstract String getUserInfoUrl();
    // 액세스 토큰으로 사용자 정보를 조회하고 OAuth2UserInfo 객체로 반환
    protected abstract OAuth2UserInfo extractUserInfo(Map<String, Object> attributes);

    @Override
    public OAuth2UserInfo getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        Map<String, Object> userAttributes = getUserAttributes(accessToken);

        return extractUserInfo(userAttributes);
    }

    @SuppressWarnings("unchecked")
    private String getAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", getClientId());
        formData.add("client_secret", getClientSecret());
        formData.add("code", code);

        Map<String, Object> response = webClient.post()
                .uri(getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(error -> new EntityNotFoundException(ErrorCode.SOCIAL_LOGIN_TOKEN_NOT_FOUND))
                )
                .bodyToMono(Map.class)
                .block();
        if (response == null || !response.containsKey("access_token")) {
            throw new EntityNotFoundException(ErrorCode.SOCIAL_LOGIN_TOKEN_NOT_FOUND);
        }

        return (String) response.get("access_token");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserAttributes(String accessToken) {
        return webClient.get()
                .uri(getUserInfoUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(error -> new EntityNotFoundException(ErrorCode.SOCIAL_LOGIN_TOKEN_NOT_FOUND))
                )
                .bodyToMono(Map.class)
                .block();
    }
}
