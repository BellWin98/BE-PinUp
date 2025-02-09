package com.pinup.domain.friend.controller;

import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.domain.friend.service.FriendShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
@Tag(name = "핀버디 관련 API", description = "핀버디 목록 조회, 핀버디 삭제, 내 핀버디 목록 조회")
public class FriendShipController {

    private final FriendShipService friendShipService;

    @GetMapping("/{userId}")
    @Operation(summary = "유저 핀버디 목록 조회 API", description = "유저 ID로 유저의 핀버디 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "해당 유저의 핀버디 목록을 조회하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = MemberResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> getUserAllFriends(
            @Schema(description = "유저 ID", example = "1")
            @PathVariable Long userId
    ) {
        List<MemberResponse> result = friendShipService.getUserAllFriends(userId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USER_PIN_BUDDY_LIST_SUCCESS, result));
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "핀버디 삭제 API", description = "해당 유저 ID의 핀버디 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "핀버디를 삭제하였습니다."
            )
    })
    public ResponseEntity<ResultResponse> removeFriend(
            @Schema(description = "핀버디 ID", example = "1")
            @PathVariable Long friendId
    ) {
        friendShipService.removeFriend(friendId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.REMOVE_PIN_BUDDY_SUCCESS));
    }

    @GetMapping
    @Operation(summary = "나의 핀버디 목록 조회 API", description = "나의 핀버디 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "나의 핀버디 목록을 조회하였습니다.",
                    content = {
                        @Content(schema = @Schema(implementation = MemberResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> getMyAllFriends() {
        List<MemberResponse> result = friendShipService.getMyAllFriends();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_PIN_BUDDY_LIST_SUCCESS, result));
    }

    @GetMapping("/search")
    @Operation(summary = "닉네임으로 내 핀버디 정보 조회 API", description = "나의 핀버디 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "나의 핀버디 목록을 조회하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = MemberResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> searchMyFriendInfoByNickname(
            @Schema(description = "유저 닉네임", example = "bellwin")
            @RequestParam("query") String query
    ) {
        MemberResponse result = friendShipService.searchMyFriendInfoByNickname(query);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_PIN_BUDDY_INFO_SUCCESS, result));
    }
}
