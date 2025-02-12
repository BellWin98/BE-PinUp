package com.pinup.domain.bookmark.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "북마크 삭제 DTO", description = "북마크를 삭제하기 위한 요청 DTO")
public class BookMarkDeleteRequest {

    @NotBlank(message = "카카오맵 장소 ID를 입력하세요.")
    @Schema(description = "카카오맵에서 부여한 장소 고유 ID", example = "480323354")
    private String kakaoPlaceId;
}