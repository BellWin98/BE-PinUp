package com.pinup.domain.member.dto.request;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpRequest {

    private String email;

    @NotEmpty(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotEmpty(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,12}$", message = "닉네임은 특수문자를 제외한 2~12자리여야 합니다.")
    private String nickname;

    @NotEmpty(message = "소셜 ID는 필수 입력값입니다.")
    private String socialId;

    private String profileImageUrl;

    @NotEmpty(message = "로그인 타입은 필수 입력값입니다.")
    private String loginType;

    private String termsOfMarketing;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .providerId(socialId)
                .loginType(LoginType.getLoginType(loginType))
                .termsOfMarketing(termsOfMarketing)
                .build();
    }
}
