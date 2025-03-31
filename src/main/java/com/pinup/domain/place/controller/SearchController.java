package com.pinup.domain.place.controller;

import com.pinup.domain.place.dto.request.PlaceSearchRequest;
import com.pinup.domain.place.dto.request.SearchRequestDto;
import com.pinup.domain.place.dto.response.PlaceSearchResponse;
import com.pinup.domain.place.service.SearchService;
import com.pinup.global.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.pinup.global.response.ResultCode.GET_PLACES_SUCCESS;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<ResultResponse> searchPlaces(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) List<Long> tags,
            @RequestParam(required = false, defaultValue = "distance") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        PlaceSearchRequest request = PlaceSearchRequest.builder()
                .keyword(keyword)
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .categories(categories)
                .tags(tags)
                .sort(sort)
                .page(page)
                .size(size)
                .build();
        Page<PlaceSearchResponse> placeSearchResponses = searchService.searchPlacesByKeyword(request);
    }
/*    @GetMapping
    public ResponseEntity<ResultResponse> searchPlacesByKeyword(@Valid SearchRequestDto searchRequestDto) {
        return ResponseEntity.ok(ResultResponse.of(GET_PLACES_SUCCESS,
                searchService.searchPlacesByKeyword(searchRequestDto)));
    }*/

}
