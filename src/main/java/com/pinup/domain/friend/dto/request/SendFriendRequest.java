package com.pinup.domain.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(title = "핀버디 신청 DTO", description = "유저의 고유 ID로 핀버디 신청")
public class SendFriendRequest {

    @Schema(description = "핀버디 고유 ID", example = "1")
    private Long receiverId;
}
