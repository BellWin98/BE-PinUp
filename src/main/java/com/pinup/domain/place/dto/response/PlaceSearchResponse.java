package com.pinup.domain.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceSearchResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distance; // 거리 (km)
    private List<CategoryInfo> categories;
    private Double averageRating;
    private Integer reviewCount;

    @Getter
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }
}
