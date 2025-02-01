package com.pinup.dto.request;

import com.pinup.entity.Chat;
import com.pinup.entity.Member;
import com.pinup.entity.Room;
import lombok.Data;

@Data
public class ChatRequest {
    private Long roomId;
    private String message;
    private String sender;

    public Chat toEntity(Room room, Member member) {
        return Chat.builder()
                .room(room)
                .message(message)
                .sender(member.getNickname())
                .build();
    }
}
