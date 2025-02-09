package com.pinup.domain.friend.dto.response;

import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.friend.entity.FriendRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(title = "친구요청 조회 응답 DTO", description = "친구요청 ID, 친구요청 상태, 친구요청자 정보, 친구요청 수신자 정보")
public class FriendRequestResponse {

    @Schema(description = "친구요청 ID")
    private Long id;

    @Schema(description = "친구요청 상태")
    private String friendRequestStatus;

    @Schema(description = "친구요청자 정보")
    private MemberResponse sender;

    @Schema(description = "친구요청 수신자 정보")
    private MemberResponse receiver;

    public static FriendRequestResponse from(FriendRequest friendRequest) {
        return FriendRequestResponse.builder()
                .id(friendRequest.getId())
                .friendRequestStatus(friendRequest.getFriendRequestStatus().name())
                .sender(MemberResponse.from(friendRequest.getSender()))
                .receiver(MemberResponse.from(friendRequest.getReceiver()))
                .build();
    }
}
