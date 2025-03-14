package com.pinup.domain.friend.controller;

import com.pinup.domain.friend.service.FriendShipService;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
@Tag(name = "핀버디 관련 API", description = "핀버디 목록 조회, 핀버디 삭제, 내 핀버디 목록 조회")
public class FriendShipController {

    private final FriendShipService friendShipService;

    @Operation(summary = "유저 핀버디 목록 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MemberResponse.class))})
    @GetMapping("/{memberId}")
    public ResponseEntity<ResultResponse> getAllFriendsOfMember(
            @Schema(description = "유저 ID", example = "1")
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_USER_PIN_BUDDY_LIST_SUCCESS,
                friendShipService.getAllFriendsOfMember(memberId))
        );
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "핀버디 삭제 API")
    public ResponseEntity<ResultResponse> removeFriend(
            @Schema(description = "핀버디 ID", example = "1")
            @PathVariable Long friendId
    ) {
        friendShipService.removeFriend(friendId);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.REMOVE_PIN_BUDDY_SUCCESS));
    }

    @Operation(summary = "나의 핀버디 정보 조회 API")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = MemberResponse.class))})
    @GetMapping("/search")
    public ResponseEntity<ResultResponse> searchMyFriendInfoByNickname(
            @Schema(description = "유저 닉네임", example = "bellwin")
            @RequestParam("nickname") String nickname
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_MY_PIN_BUDDY_INFO_SUCCESS,
                friendShipService.searchMyFriendInfoByNickname(nickname))
        );
    }
}
