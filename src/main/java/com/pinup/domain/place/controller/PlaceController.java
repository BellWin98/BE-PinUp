package com.pinup.domain.place.controller;

import com.pinup.domain.place.dto.request.MapBoundDto;
import com.pinup.domain.place.dto.request.MapViewDto;
import com.pinup.domain.place.dto.response.*;
import com.pinup.domain.place.service.PlaceService;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pinup.global.response.ResultCode.GET_PLACES_SUCCESS;
import static com.pinup.global.response.ResultCode.GET_PLACE_DETAIL_SUCCESS;

@Tag(name = "장소 API", description = "장소 목록 조회(리뷰있는 가게만), 장소 목록 조회(전체) 장소 상세 조회")
@RequestMapping("/api/places")
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(summary = "리뷰 있는 장소 목록 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MapPlaceResponse.class))})
    @GetMapping
    public ResponseEntity<ResultResponse> getMapPlaces(
            @Schema(description = "키워드", example = "스타벅스")
            @RequestParam(defaultValue = "", value = "query", required = false) String query,

            @Schema(description = "카테고리", example = "ALL")
            @RequestParam(defaultValue = "ALL", value = "category", required = false) String category,

            @Schema(description = "정렬조건", example = "NEAR")
            @RequestParam(defaultValue = "NEAR", value = "sort", required = false) String sort,

            @Schema(description = "SW 위도", example = "37.548608")
            @RequestParam(value = "swLatitude") double swLatitude,

            @Schema(description = "SW 경도", example = "126.795968")
            @RequestParam(value = "swLongitude") double swLongitude,

            @Schema(description = "NE 위도", example = "37.569940")
            @RequestParam(value = "neLatitude") double neLatitude,

            @Schema(description = "NE 경도", example = "126.844764")
            @RequestParam(value = "neLongitude") double neLongitude,

            @Schema(description = "현 위치 위도", example = "37.562651")
            @RequestParam(value = "currentLatitude", required = false) Double currentLatitude,

            @Schema(description = "현 위치 경도", example = "126.826539")
            @RequestParam(value = "currentLongitude", required = false) Double currentLongitude
/*            @Schema(description = "현재 페이지", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Schema(description = "한 페이지에 노출할 데이터 건수", example = "20")
            @RequestParam(defaultValue = "20") int size*/
    ) {
//        Pageable pageable = PageRequest.of(page, size);
        MapBoundDto mapBound = MapBoundDto.builder()
                .neLat(neLatitude)
                .neLng(neLongitude)
                .swLat(swLatitude)
                .swLng(swLongitude)
                .currLat(currentLatitude)
                .currLng(currentLongitude)
                .build();
        List<MapPlaceResponse> result = placeService.getMapPlaces(query, category, sort, mapBound);

        return ResponseEntity.ok(ResultResponse.of(GET_PLACES_SUCCESS, result));
    }

    @GetMapping("/{kakaoPlaceId}")
    @Operation(summary = "장소 상세 조회 API", description = "카카오맵에서 부여한 고유 ID로 장소 상세 조회")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MapPlaceDetailResponse.class))})
    public ResponseEntity<ResultResponse> getMapPlaceDetail(
            @Schema(description = "카카오맵 장소 고유 ID", example = "1997608947")
            @PathVariable("kakaoPlaceId") String kakaoPlaceId,

            @Schema(description = "현 위치 위도", example = "37.562651")
            @RequestParam(value = "currentLatitude", required = false) Double currentLatitude,

            @Schema(description = "현 위치 경도", example = "126.826539")
            @RequestParam(value = "currentLongitude", required = false) Double currentLongitude
    ) {
        MapPlaceDetailResponse result = placeService.getMapPlaceDetail(kakaoPlaceId, currentLatitude, currentLongitude);
        return ResponseEntity.ok(ResultResponse.of(GET_PLACE_DETAIL_SUCCESS, result));
    }

    @Operation(summary = "전체 장소 목록 조회 API", description = "리뷰 작성할 장소 조회 시 사용 / 카카오맵 API 호출")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = PlaceResponse.class))})
    @GetMapping("/keyword")
    public ResponseEntity<ResultResponse> getEntirePlaces(
            @Schema(description = "검색어", example = "하루카페") @RequestParam(value = "query") String query
    ) {
        List<PlaceResponse> result = placeService.getEntirePlaces(query);
        return ResponseEntity.ok(ResultResponse.of(GET_PLACES_SUCCESS, result));
    }

    @Operation(summary = "지도 상 장소 목록 조회 API", description = "클러스터링 적용")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = TotalPlaceResponse.class))})
    @GetMapping("/clustered")
    public ResponseEntity<ResultResponse> getClusteredMapPlacesWithinBounds(
            @Schema(description = "키워드", example = "스타벅스")
            @RequestParam(defaultValue = "", value = "query", required = false) String query,

            @Schema(description = "카테고리", example = "ALL")
            @RequestParam(defaultValue = "ALL", value = "category", required = false) String category,

            @Schema(description = "정렬조건", example = "NEAR")
            @RequestParam(defaultValue = "NEAR", value = "sort", required = false) String sort,

            @Schema(description = "지도 중심 위도", example = "37.3576046")
            @RequestParam(value = "centerLatitude") double centerLatitude,

            @Schema(description = "지도 중심 경도", example = "126.95550245")
            @RequestParam(value = "centerLongitude") double centerLongitude,

            @Schema(description = "SW 위도", example = "36.5258844")
            @RequestParam(value = "swLatitude") double swLatitude,

            @Schema(description = "SW 경도", example = "126.354001")
            @RequestParam(value = "swLongitude") double swLongitude,

            @Schema(description = "NE 위도", example = "38.1893248")
            @RequestParam(value = "neLatitude") double neLatitude,

            @Schema(description = "NE 경도", example = "127.5570039")
            @RequestParam(value = "neLongitude") double neLongitude,

            @Schema(description = "현 위치 위도", example = "37.5626316")
            @RequestParam(value = "currentLatitude", required = false) Double currentLatitude,

            @Schema(description = "현 위치 경도", example = "126.8357822")
            @RequestParam(value = "currentLongitude", required = false) Double currentLongitude,

            @Schema(description = "줌 레벨", example = "0")
            @RequestParam(value = "zoomLevel") int zoomLevel
    ) {
        MapViewDto mapView = MapViewDto.builder()
                .centerLat(centerLatitude)
                .centerLng(centerLongitude)
                .neLat(neLatitude)
                .neLng(neLongitude)
                .swLat(swLatitude)
                .swLng(swLongitude)
                .currLat(currentLatitude)
                .currLng(currentLongitude)
                .zoomLevel(zoomLevel + 1)
                .build();
        TotalPlaceResponse result = placeService.getClusteredMapPlacesWithinBounds(query, category, sort, mapView);

        return ResponseEntity.ok(ResultResponse.of(GET_PLACES_SUCCESS, result));
    }
}
