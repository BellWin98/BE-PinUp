package com.pinup.domain.review.controller;

import com.pinup.domain.place.dto.request.PlaceRequest;
import com.pinup.domain.review.dto.request.ReviewRequest;
import com.pinup.domain.review.dto.response.ReviewResponse;
import com.pinup.domain.review.service.ReviewService;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "리뷰 API", description = "리뷰 등록 및 조회")
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록 API", description = "리뷰 등록 성공 시 리뷰 ID 반환")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = String.class))})
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> register(
            @Valid @RequestPart ReviewRequest reviewRequest,
            @Valid @RequestPart PlaceRequest placeRequest,
            @RequestPart(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.CREATE_REVIEW_SUCCESS,
                reviewService.register(reviewRequest, placeRequest, multipartFiles))
        );
    }

    @Operation(summary = "리뷰 상세 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = ReviewResponse.class))})
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResultResponse> getReviewDetail(@PathVariable Long reviewId) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_REVIEW_DETAIL_SUCCESS,
                reviewService.getReviewById(reviewId))
        );
    }
}