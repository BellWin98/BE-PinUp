package com.pinup.domain.member.controller;

import com.pinup.domain.member.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.domain.member.dto.response.FeedResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.SearchMemberResponse;
import com.pinup.domain.member.service.MemberService;
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

    @Operation(summary = "핀버디 정보 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MemberResponse.class))})
    @GetMapping("/{friendId}")
    public ResponseEntity<ResultResponse> getMemberInfo(@PathVariable("friendId") Long friendId) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_USER_INFO_SUCCESS,
                memberService.getMemberInfo(friendId))
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

    @Operation(summary = "마이피드 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FeedResponse.class))})
    @GetMapping("/me/feed")
    public ResponseEntity<ResultResponse> getMyFeed() {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_MY_FEED_SUCCESS,
                memberService.getMyFeed())
        );
    }

    @Operation(summary = "유저 피드 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FeedResponse.class))})
    @GetMapping("/{memberId}/feed")
    public ResponseEntity<ResultResponse> getMemberFeed(@PathVariable Long memberId) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_MEMBER_FEED_SUCCESS,
                memberService.getMemberFeed(memberId))
        );
    }
}
