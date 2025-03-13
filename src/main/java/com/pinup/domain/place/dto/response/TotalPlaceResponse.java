package com.pinup.domain.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TotalPlaceResponse {
    private List<PlaceClusterDto> clusters;
    private List<MapPlaceResponse> places;
}
