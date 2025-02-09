package com.pinup.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberJoinRequest {

    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "유저명", example = "testName")
    private String name;

    @Schema(description = "유저 닉네임", example = "testNickname")
    private String nickname;

    @Schema(description = "프로필 사진", example = "https://pinup-bucket-dev.s3.ap-northeast-2.amazonaws.com/profiles/2025/02/06/ae8679e5-482e-4b1f-a424-7dc01f665bca.png")
    private String profileImageUrl;

    @Schema(description = "비밀번호", example = "test")
    private String password;
}
