package com.pinup.domain.member.dto.request;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String name;
    private String nickname;
    private String socialId;
    private String loginType;
    private String profileImageUrl;
    private String termsOfMarketing;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .socialId(socialId)
                .profileImageUrl(profileImageUrl)
                .loginType(LoginType.getLoginType(loginType))
                .termsOfMarketing(termsOfMarketing)
                .build();
    }
}
