package com.pinup.domain.bookmark.controller;

import com.pinup.domain.bookmark.dto.request.BookMarkCreateRequest;
import com.pinup.domain.bookmark.dto.response.BookMarkResponse;
import com.pinup.domain.bookmark.service.BookMarkService;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Tag(name = "북마크 관련 API")
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @Operation(summary = "북마크 등록 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = Long.class))})
    @PostMapping
    public ResponseEntity<ResultResponse> create(
            @Valid @RequestBody BookMarkCreateRequest request
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.CREATE_BOOKMARK_SUCCESS,
                bookMarkService.create(request.getKakaoPlaceId()))
        );
    }

    @Operation(summary = "내 북마크 조회 API", description = "카테고리와 정렬 조건으로 북마크 목록을 필터링하여 조회합니다")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = BookMarkResponse.class))})
    @GetMapping
    public ResponseEntity<ResultResponse> getFilteredBookmarks(
            @Schema(description = "카테고리 (ALL/RESTAURANT/CAFE)", example = "ALL")
            @RequestParam(defaultValue = "ALL", value = "category", required = false) String category,

            @Schema(description = "정렬조건 (NEAR/LATEST/STAR_HIGH/STAR_LOW)", example = "NEAR")
            @RequestParam(defaultValue = "NEAR", value = "sort", required = false) String sort,

            @Schema(description = "현 위치 위도", example = "37.562651")
            @RequestParam(value = "currentLatitude", required = false) Double currentLatitude,

            @Schema(description = "현 위치 경도", example = "126.826539")
            @RequestParam(value = "currentLongitude", required = false) Double currentLongitude
    ) {
        List<BookMarkResponse> bookmarks = bookMarkService.getFilteredBookmarks(
                category, sort, currentLatitude, currentLongitude
        );
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_BOOKMARK_SUCCESS, bookmarks));
    }

    @Operation(summary = "북마크 삭제 API", description = "특정 북마크를 삭제합니다")
    @DeleteMapping("/{kakaoPlaceId}")
    public ResponseEntity<ResultResponse> delete(
            @Schema(description = "카카오맵에서 부여한 장소 고유 ID", example = "480323354")
            @PathVariable String kakaoPlaceId
    ) {
        bookMarkService.delete(kakaoPlaceId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_BOOKMARK_SUCCESS));
    }
}