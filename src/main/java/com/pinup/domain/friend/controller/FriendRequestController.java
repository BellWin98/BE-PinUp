package com.pinup.domain.friend.controller;

import com.pinup.domain.friend.dto.request.SendFriendRequest;
import com.pinup.domain.friend.dto.response.FriendRequestResponse;
import com.pinup.domain.friend.service.FriendRequestService;
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


@Tag(name = "핀버디 신청 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/friend-requests")
@RestController
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @Operation(summary = "받은 핀버디 신청 목록 조회 API", description = "상태가 PENDING 인 받은 핀버디 신청 목록 조회")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FriendRequestResponse.class))})
    @GetMapping("/received")
    public ResponseEntity<ResultResponse> getReceivedFriendRequests() {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_RECEIVED_PIN_BUDDY_REQUEST_LIST_SUCCESS,
                friendRequestService.getReceivedFriendRequests())
        );
    }

    @Operation(summary = "보낸 핀버디 신청 목록 조회 API", description = "상태가 PENDING 인 보낸 핀버디 신청 목록 조회")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FriendRequestResponse.class))})
    @GetMapping("/sent")
    public ResponseEntity<ResultResponse> getSentFriendRequests() {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.GET_SENT_PIN_BUDDY_REQUEST_LIST_SUCCESS,
                friendRequestService.getSentFriendRequests())
        );
    }

    @Operation(summary = "핀버디 신청 API", description = "핀버디 요청 상태 PENDING 으로 신규 생성")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FriendRequestResponse.class))})
    @PostMapping("/send")
    public ResponseEntity<ResultResponse> sendFriendRequest(@RequestBody SendFriendRequest sendFriendRequest) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.REQUEST_PIN_BUDDY_SUCCESS,
                friendRequestService.sendFriendRequest(sendFriendRequest.getReceiverId()))
        );
    }

    @Operation(summary = "핀버디 신청 취소 API", description = "핀버디 요청 삭제")
    @DeleteMapping("/{friendRequestId}")
    public ResponseEntity<ResultResponse> cancelFriendRequest(@PathVariable("friendRequestId") Long friendRequestId) {
        friendRequestService.cancelFriendRequest(friendRequestId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CANCEL_PIN_BUDDY_SUCCESS));
    }

    @Operation(summary = "핀버디 신청 수락 API", description = "핀버디 요청 상태를 ACCEPTED 로 변경")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FriendRequestResponse.class))})
    @PatchMapping("/{friendRequestId}/accept")
    public ResponseEntity<ResultResponse> acceptFriendRequest(
            @Schema(description = "DB에 등록된 친구요청 고유 ID", example = "1")
            @PathVariable("friendRequestId") Long friendRequestId
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.ACCEPT_PIN_BUDDY_SUCCESS,
                friendRequestService.acceptFriendRequest(friendRequestId))
        );
    }

    @Operation(summary = "핀버디 신청 거절 API", description = "핀버디 요청 상태를 REJECTED 로 변경")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = FriendRequestResponse.class))})
    @PatchMapping("/{friendRequestId}/reject")
    public ResponseEntity<ResultResponse> rejectFriendRequest(
            @Schema(description = "DB에 등록된 친구요청 고유 ID", example = "1")
            @PathVariable("friendRequestId") Long friendRequestId
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.REJECT_PIN_BUDDY_SUCCESS,
                friendRequestService.rejectFriendRequest(friendRequestId))
        );
    }
}
