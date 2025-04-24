package com.pinup.domain.review.controller;

import com.pinup.domain.place.dto.request.PlaceRequest;
import com.pinup.domain.review.dto.request.ReviewRegisterRequest;
import com.pinup.domain.review.dto.request.ReviewRequest;
import com.pinup.domain.review.dto.request.UpdateReviewRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 API", description = "리뷰 등록 및 조회")
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "리뷰 등록 성공 시 kakaoPlaceId 반환")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = String.class))})
    @PostMapping
    public ResponseEntity<ResultResponse> register(@RequestBody ReviewRegisterRequest reviewRegisterRequest) {
        ReviewRequest reviewRequest = reviewRegisterRequest.getReviewRequest();
        PlaceRequest placeRequest = reviewRegisterRequest.getPlaceRequest();

        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.CREATE_REVIEW_SUCCESS,
                reviewService.register(reviewRequest, placeRequest))
        );
    }

    @Operation(summary = "리뷰 상세 조회")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = ReviewResponse.class))})
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResultResponse> getReviewDetail(@PathVariable Long reviewId) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_REVIEW_DETAIL_SUCCESS,
                reviewService.getReviewById(reviewId))
        );
    }

    @Operation(summary = "리뷰 수정", description = "기존에 작성된 리뷰 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResultResponse> update(@PathVariable Long reviewId,
                                                 @Valid @RequestBody UpdateReviewRequest updateReviewRequest) {
        reviewService.update(reviewId, updateReviewRequest);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.UPDATE_REVIEW_SUCCESS));
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰, 리뷰 이미지 전부 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResultResponse> delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_REVIEW_SUCCESS));
    }
}