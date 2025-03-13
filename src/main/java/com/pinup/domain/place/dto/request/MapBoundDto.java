package com.pinup.domain.place.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapBoundDto {
    private Double neLat;
    private Double neLng;
    private Double swLat;
    private Double swLng;
    private Double currLat;
    private Double currLng;
}
