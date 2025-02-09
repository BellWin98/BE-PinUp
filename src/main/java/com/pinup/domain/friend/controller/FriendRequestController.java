package com.pinup.domain.friend.controller;

import com.pinup.domain.friend.dto.request.SendFriendRequest;
import com.pinup.domain.friend.dto.response.FriendRequestResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.domain.friend.service.FriendRequestService;
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
@RequiredArgsConstructor
@RequestMapping("/api/friend-requests")
@Tag(name = "핀버디 신청 관련 API", description = "핀버디 신청, 수락, 거절, 받은 신청 목록 조회")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @PostMapping("/send")
    @Operation(summary = "핀버디 신청 API", description = "친구요청 상태 PENDING 으로 신규 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "핀버디 신청이 완료되었습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = FriendRequestResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> sendFriendRequest(
            @RequestBody SendFriendRequest sendFriendRequest) {
        FriendRequestResponse result = friendRequestService.sendFriendRequest(sendFriendRequest.getReceiverId());

        return ResponseEntity.ok(ResultResponse.of(ResultCode.REQUEST_PIN_BUDDY_SUCCESS, result));
    }

    @PatchMapping("/{friendRequestId}/accept")
    @Operation(summary = "핀버디 신청 수락 API", description = "친구요청 상태를 ACCEPTED 로 변경")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "핀버디 신청을 수락했습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = FriendRequestResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> acceptFriendRequest(
            @Schema(description = "DB에 등록된 친구요청 고유 ID", example = "1")
            @PathVariable("friendRequestId") Long friendRequestId
    ) {
        FriendRequestResponse result = friendRequestService.acceptFriendRequest(friendRequestId);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.ACCEPT_PIN_BUDDY_SUCCESS, result));
    }

    @PatchMapping("/{friendRequestId}/reject")
    @Operation(summary = "핀버디 신청 거절 API", description = "친구요청 상태를 REJECTED 로 변경")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "핀버디 신청을 거절했습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = FriendRequestResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> rejectFriendRequest(
            @Schema(description = "DB에 등록된 친구요청 고유 ID", example = "1")
            @PathVariable("friendRequestId") Long friendRequestId
    ) {
        FriendRequestResponse result = friendRequestService.rejectFriendRequest(friendRequestId);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.REJECT_PIN_BUDDY_SUCCESS, result));
    }

    @GetMapping("/received")
    @Operation(summary = "받은 핀버디 신청 목록 조회 API", description = "상태가 PENDING 인 받은 핀버디 신청 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "받은 핀버디 신청 목록을 조회하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = FriendRequestResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> getReceivedFriendRequests() {
        List<FriendRequestResponse> results = friendRequestService.getReceivedFriendRequests();

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECEIVED_PIN_BUDDY_REQUEST_LIST_SUCCESS, results));
    }
}
