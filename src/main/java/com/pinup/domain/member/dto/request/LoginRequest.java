package com.pinup.domain.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "소셜 ID는 필수 입력값입니다.")
    private String socialId;
}
