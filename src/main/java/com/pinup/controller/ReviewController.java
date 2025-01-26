package com.pinup.controller;

import com.pinup.dto.request.PlaceRequest;
import com.pinup.dto.request.ReviewRequest;
import com.pinup.dto.response.ReviewPreviewResponse;
import com.pinup.dto.response.ReviewResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 API", description = "리뷰 등록 및 조회")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 등록 API", description = "리뷰 등록 성공 시 리뷰 ID 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 등록에 성공하였습니다.")
    })
    public ResponseEntity<ResultResponse> register(
            @Valid @RequestPart ReviewRequest reviewRequest,
            @Valid @RequestPart PlaceRequest placeRequest,
            @RequestPart(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles
    ) {
        String kakaoPlaceId = reviewService.register(reviewRequest, placeRequest, multipartFiles);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CREATE_REVIEW_SUCCESS, kakaoPlaceId));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회 API")
    public ResponseEntity<ResultResponse> getReviewDetail(@PathVariable Long reviewId) {
        ReviewResponse result = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_REVIEW_DETAIL_SUCCESS, result));
    }

    @GetMapping("/my/photo")
    @Operation(summary = "내 포토 리뷰 미리보기 목록 API")
    public ResponseEntity<ResultResponse> getMyPhotoReviewPreviews() {
        List<ReviewPreviewResponse> result = reviewService.getPhotoReviewPreviews();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_PHOTO_REVIEW_PREVIEW_SUCCESS, result));
    }

    @GetMapping("/my/text")
    @Operation(summary = "내 텍스트 리뷰 목록 API")
    public ResponseEntity<ResultResponse> getMyTextReviews() {
        List<ReviewResponse> result = reviewService.getMyTextReviewDetails();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_TEXT_REVIEW_SUCCESS, result));
    }

    @GetMapping("/members/{memberId}/photo/preview")
    @Operation(summary = "멤버의 포토 리뷰 미리보기 목록 API")
    public ResponseEntity<ResultResponse> getMemberPhotoReviewPreviews(@PathVariable Long memberId) {
        List<ReviewPreviewResponse> result = reviewService.getMemberPhotoReviewPreviews(memberId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MEMBER_PHOTO_REVIEW_PREVIEW_SUCCESS, result));
    }

    @GetMapping("/members/{memberId}/text")
    @Operation(summary = "멤버의 텍스트 리뷰 목록 API")
    public ResponseEntity<ResultResponse> getMemberTextReviews(@PathVariable Long memberId) {
        List<ReviewResponse> result = reviewService.getMemberTextReviews(memberId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MEMBER_TEXT_REVIEW_SUCCESS, result));
    }
}