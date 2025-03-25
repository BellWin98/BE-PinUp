package com.pinup.domain.member.controller;

import com.pinup.domain.member.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.domain.member.dto.request.UpdateProfileRequest;
import com.pinup.domain.member.dto.response.MemberInfoResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.SearchMemberResponse;
import com.pinup.domain.member.service.MemberService;
import com.pinup.domain.review.dto.response.PhotoReviewResponse;
import com.pinup.domain.review.dto.response.TextReviewResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "유저 API")
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "닉네임으로 유저 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = SearchMemberResponse.class))})
    @GetMapping("/search")
    public ResponseEntity<ResultResponse> searchMembers(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_USER_INFO_SUCCESS,
                memberService.searchMembers(nickname))
        );
    }

    @Operation(summary = "내 정보 조회 API", description = "자동 로그인 시 사용")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MemberInfoResponse.class))})
    @GetMapping
    public ResponseEntity<ResultResponse> getMyInfo() {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_MY_INFO_SUCCESS,
                memberService.getMyInfo())
        );
    }

    @Operation(summary = "유저 정보 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MemberInfoResponse.class))})
    @GetMapping("/{memberId}")
    public ResponseEntity<ResultResponse> getMemberInfo(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_USER_INFO_SUCCESS,
                memberService.getMemberInfo(memberId))
        );
    }

    @Operation(summary = "닉네임 중복 여부 확인 API", description = "true: 닉네임 중복 / false: 사용 가능한 닉네임")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = boolean.class))})
    @GetMapping("/nickname/check")
    public ResponseEntity<ResultResponse> checkNicknameDuplicate(@RequestParam(value = "nickname") String nickname) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_NICKNAME_DUPLICATE_SUCCESS,
                memberService.checkNicknameDuplicate(nickname))
        );
    }

    @Operation(summary = "유저 프로필 수정 API")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> updateProfile(
            @Valid @RequestPart UpdateProfileRequest updateProfileRequest,
            @RequestPart(name = "multipartFile", required = false) MultipartFile multipartFile
    ) {
        memberService.updateProfile(updateProfileRequest, multipartFile);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.UPDATE_MEMBER_INFO_SUCCESS));
    }

    @Operation(summary = "소셜 로그인 후처리 API", description = "닉네임, 프로필사진, 마케팅 수신동의 여부 등록")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MemberResponse.class))})
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> updateInfoAfterLogin(
            @Valid @RequestPart UpdateMemberInfoAfterLoginRequest request,
            @RequestPart(name = "multipartFile", required = false) MultipartFile multipartFile
    ){
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.UPDATE_MEMBER_INFO_SUCCESS,
                memberService.updateInfoAfterLogin(request, multipartFile))
        );
    }

    @Operation(summary = "유저 텍스트 리뷰 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = TextReviewResponse.class))})
    @GetMapping("/{memberId}/text-reviews")
    public ResponseEntity<ResultResponse> getMemberTextReviews(
            @PathVariable Long memberId,

            @Schema(description = "현재 페이지", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Schema(description = "한 페이지에 노출할 데이터 건수", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_TEXT_REVIEW_SUCCESS,
                memberService.getMemberTextReviews(pageable, memberId))
        );
    }

    @Operation(summary = "나의 텍스트 리뷰 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = TextReviewResponse.class))})
    @GetMapping("/me/text-reviews")
    public ResponseEntity<ResultResponse> getMyTextReviews(
            @Schema(description = "현재 페이지", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Schema(description = "한 페이지에 노출할 데이터 건수", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_TEXT_REVIEW_SUCCESS,
                memberService.getMyTextReviews(pageable))
        );
    }

    @Operation(summary = "유저 포토 리뷰 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = PhotoReviewResponse.class))})
    @GetMapping("/{memberId}/photo-reviews")
    public ResponseEntity<ResultResponse> getPhotoReviews(
            @PathVariable Long memberId,

            @Schema(description = "현재 페이지", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Schema(description = "한 페이지에 노출할 데이터 건수", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_PHOTO_REVIEW_SUCCESS,
                memberService.getPhotoReviews(pageable, memberId))
        );
    }

    @Operation(summary = "나의 포토 리뷰 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = PhotoReviewResponse.class))})
    @GetMapping("/me/photo-reviews")
    public ResponseEntity<ResultResponse> getMyPhotoReviews(
            @Schema(description = "현재 페이지", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Schema(description = "한 페이지에 노출할 데이터 건수", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_PHOTO_REVIEW_SUCCESS,
                memberService.getMyPhotoReviews(pageable))
        );
    }
}
