package com.pinup.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMemberInfoAfterLoginRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @NotBlank(message = "마케팅 정보 수신 동의 여부를 입력해주세요.")
    @Schema(description = "마케팅 정보 수신 동의", example = "Y / N")
    private String termsOfMarketing;
}
