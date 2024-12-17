package com.pinup.controller;

import com.pinup.dto.request.MemberInfoUpdateRequest;
import com.pinup.dto.response.MemberResponse;
import com.pinup.dto.response.ProfileResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "")
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/search")
    @Operation(summary = "닉네임으로 유저 조회 API", description = "닉네임으로 핀업 유저를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 조회에 성공하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = MemberResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> searchMembers(@RequestParam("nickname") String nickname) {
        MemberResponse searchResult = memberService.searchMembers(nickname);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USER_INFO_SUCCESS, searchResult));
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 유저 조회 API", description = "내 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "현재 로그인한 유저 정보 조회에 성공하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = MemberResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> getCurrentMemberInfo() {
        MemberResponse currentMember = memberService.getCurrentMemberInfo();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_LOGIN_USER_INFO_SUCCESS, currentMember));
    }

    @GetMapping("/{friendId}")
    @Operation(summary = "핀버디 단건 정보 조회 API", description = "핀버디 ID로 핀버디 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 조회에 성공하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = MemberResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> getMemberInfo(@PathVariable("friendId") Long friendId) {
        MemberResponse currentMember = memberService.getMemberInfo(friendId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USER_INFO_SUCCESS, currentMember));
    }

    @DeleteMapping
    @Operation(summary = "유저 삭제 API", description = "유저 회원정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 삭제에 성공하였습니다."
            )
    })
    public ResponseEntity<ResultResponse> deleteMember() {
        memberService.deleteMember();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_USER_SUCCESS));
    }

    @GetMapping("/nickname/check")
    @Operation(summary = "닉네임 중복 여부 확인 API", description = "true: 닉네임 중복 / false: 사용 가능한 닉네임")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "닉네임 중복 여부 확인에 성공하였습니다."
            )
    })
    public ResponseEntity<ResultResponse> checkNicknameDuplicate(@RequestParam(value = "nickname") String nickname) {
        boolean isDuplicate = memberService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_NICKNAME_DUPLICATE_SUCCESS, isDuplicate));
    }

    @PutMapping
    @Operation(summary = "유저 정보 업데이트 API", description = "닉네임, 소개글 함께 업데이트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 수정에 성공하였습니다."
            )
    })
    public ResponseEntity<ResultResponse> updateMemberInfo(@Valid MemberInfoUpdateRequest request,
                                                           @RequestParam(value = "multipartFiles") MultipartFile image
    ) {
        MemberResponse response = memberService.updateMemberInfo(request, image);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.UPDATE_MEMBER_INFO_SUCCESS, response));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ResultResponse> getMyFeed() {
        ProfileResponse response = memberService.getMyProfile();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_FEED_SUCCESS, response));
    }

    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ResultResponse> getFeed(@PathVariable Long memberId) {
        ProfileResponse response = memberService.getProfile(memberId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USER_FEED_SUCCESS, response));
    }
}
