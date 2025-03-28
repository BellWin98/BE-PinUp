package com.pinup.domain.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResponseDto {

    private List<PlaceResponseDto> places;
    private PaginationDto pagination;

    @Getter
    @Builder
    public static class PaginationDto {
        private int page;
        private int size;
        private Long totalElements;
        private int totalPages;
    }
}
