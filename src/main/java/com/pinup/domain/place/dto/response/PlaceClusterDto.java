package com.pinup.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceClusterDto {
    private String clusterId; // 클러스터 또는 중심 장소 ID
    private Double latitude;
    private Double longitude;
    private int count; // 클러스터 내 장소 갯수
    private List<MapPlaceResponse> places; // 클러스터에 포함된 장소들
}
