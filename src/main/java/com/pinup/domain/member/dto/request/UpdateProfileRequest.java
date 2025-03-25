package com.pinup.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "수정할 닉네임을 입력해주세요.")
    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "수정할 소개글을 입력해주세요.")
    @Size(max = 150, message = "소개글은 150자 이내로 입력해주세요.")
    private String bio;
}