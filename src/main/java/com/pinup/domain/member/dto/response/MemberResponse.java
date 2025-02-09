package com.pinup.domain.member.dto.response;

import com.pinup.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "유저 정보 상세 조회 응답 DTO", description = "유저 ID, 이메일, 이름, 닉네임, 프로필 사진 URL, 소개글")
public class MemberResponse {

    @Schema(description = "유저 ID")
    private Long memberId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "프로필 사진 URL")
    private String profilePictureUrl;

    @Schema(description = "소개글")
    private String bio;

    @Schema(description = "마케팅 정보 수신 동의")
    private String termsOfMarketing;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profilePictureUrl(member.getProfileImageUrl())
                .nickname(StringUtils.hasText(member.getNickname()) ? member.getNickname() : "")
                .bio(StringUtils.hasText(member.getBio()) ? member.getBio() : "")
                .termsOfMarketing(member.getTermsOfMarketing())
                .build();
    }
}
