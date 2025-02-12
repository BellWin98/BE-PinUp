package com.pinup.domain.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "키워드로 장소 목록 조회 응답 DTO", description = "카카오맵 API로 키워드와 관련된 모든 장소 목록 조회")
public class PlaceResponseByKeyword {

    @Schema(description = "카카오맵에서 부여한 장소 고유 ID")
    private String kakaoPlaceId;

    @Schema(description = "장소명")
    private String name;

    @Schema(description = "장소 카테고리")
    private String category;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "도로명 주소")
    private String roadAddress;

    @Schema(description = "위도")
    private String latitude;

    @Schema(description = "경도")
    private String longitude;

    @Schema(description = "리뷰 수")
    private int reviewCount;

    @Schema(description = "평균 별점")
    private Double averageStarRating;
}
