package com.pinup.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequestDto {

    @NotBlank(message = "검색 키워드는 필수입니다.")
    @Schema(description = "검색 키워드", example = "맛집")
    private String keyword;

    @Min(value = -90, message = "위도는 -90보다 크거나 같아야 합니다.")
    @Max(value = 90, message = "위도는 90보다 작거나 같아야 합니다.")
    @Schema(description = "현 위치 위도", example = "37.562988")
    private Double currLat;

    @Min(value = -180, message = "경도는 -180보다 크거나 같아야 합니다.")
    @Max(value = 180, message = "경도는 180보다 작거나 같아야 합니다.")
    @Schema(description = "현 위치 경도", example = "126.8267932")
    private Double currLng;

    @Min(value = 0, message = "반경은 0보다 커야 합니다.")
    @Max(value = 50, message = "반경은 50km를 초과할 수 없습니다.")
    @Schema(description = "반경", example = "5.0")
    private Double radius = 5.0; // 기본값 5km

/*    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    private int page = 1; // 기본값 1페이지

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100을 초과할 수 없습니다.")
    private int size = 10; // 기본값 10개*/
}
