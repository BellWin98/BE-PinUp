package com.pinup.domain.friend.dto.response;

import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.friend.entity.FriendShipStatus;
import com.pinup.domain.member.dto.response.MemberResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FriendshipResponse {
    private Long id;
    private MemberResponse member;
    private MemberResponse friend;
    private FriendShipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FriendshipResponse from(FriendShip friendShip) {
        return FriendshipResponse.builder()
                .id(friendShip.getId())
                .member(MemberResponse.from(friendShip.getMember()))
                .friend(MemberResponse.from(friendShip.getFriend()))
                .status(friendShip.getStatus())
                .createdAt(friendShip.getCreatedAt())
                .updatedAt(friendShip.getUpdatedAt())
                .build();
    }
}
