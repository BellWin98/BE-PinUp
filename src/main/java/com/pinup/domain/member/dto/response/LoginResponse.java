package com.pinup.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "로그인 응답 DTO", description = "로그인 성공 시 Access/Refresh 토큰 및 유저 정보 반환")
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private MemberResponse memberResponse;
}