package com.pinup.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NormalLoginRequest {

    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "비밀번호", example = "test")
    private String password;
}
