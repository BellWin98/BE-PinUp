package com.pinup.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {
    @NotBlank(message = "소셜 로그인 제공자를 입력해주세요.")
    private String provider;

    @NotBlank(message = "인증 코드를 입력해주세요.")
    private String code;
}
