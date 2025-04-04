package com.pinup.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotBlank(message = "리뷰 내용을 입력하세요")
    @Size(min = 10, max = 200, message = "리뷰 글자 수는 최소 10자, 최대 200자입니다.")
    @Schema(description = "리뷰 내용", example = "음식이 맛있어요!")
    private String content;

    @NotNull(message = "별점을 입력하세요")
    @Schema(description = "별점(최소 0.5 ~ 최대 5.0 / 0.5점 단위)", example = "3.5")
    private double starRating;
}
