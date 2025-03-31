package com.pinup.domain.place.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceSearchRequest {
    private String keyword;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private List<Long> categories;
    private List<Long> tags;
    private String sort;
    private int page;
    private int size;
}
