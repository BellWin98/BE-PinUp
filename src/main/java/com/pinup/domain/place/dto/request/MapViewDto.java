package com.pinup.domain.place.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MapViewDto {
    private Double centerLat; // 지도 중심 위도
    private Double centerLng; // 지도 중심 경도
    private Double neLat;
    private Double neLng;
    private Double swLat;
    private Double swLng;
    private Double currLat;
    private Double currLng;
    private int zoomLevel; // 지도 줌 레벨
}
