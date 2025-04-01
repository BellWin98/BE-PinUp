package com.pinup.domain.place.controller;

import com.pinup.domain.place.dto.request.SearchRequestDto;
import com.pinup.domain.place.service.SearchService;
import com.pinup.global.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.pinup.global.response.ResultCode.GET_PLACES_SUCCESS;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/autocomplete")
    public ResponseEntity<ResultResponse> suggestByKeyword(
            @RequestParam String keyword,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(ResultResponse.of(GET_PLACES_SUCCESS,
                searchService.suggestByKeyword(keyword, size)));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> searchPlacesByKeyword(@Valid SearchRequestDto searchRequestDto) {
        return ResponseEntity.ok(ResultResponse.of(GET_PLACES_SUCCESS,
                searchService.searchPlacesByKeyword(searchRequestDto)));
    }
}
