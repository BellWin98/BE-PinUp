package com.pinup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedResponse {

    @Schema(description = "유저 개인정보")
    private MemberResponse memberResponse;

    @Schema(description = "작성한 리뷰 수")
    private int reviewCount;

    @Schema(description = "작성 리뷰 평균 별점")
    private double averageStarRating;

    @Schema(description = "핀버디 수")
    private int pinBuddyCount;

    @Schema(description = "유저가 작성한 리뷰 정보")
    private List<MemberReviewResponse> memberReviews;
}
