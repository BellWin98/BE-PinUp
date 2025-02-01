package com.pinup.dto.response;

import com.pinup.entity.Chat;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ChatResponse {
    private Long id;
    private String message;
    private String roomName;
    private String sender;
    private String sendDate;

    public static ChatResponse from(Chat chat) {
        return ChatResponse.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .roomName(chat.getRoom().getName())
                .sender(chat.getSender())
                .sendDate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(chat.getSendDate()))
                .build();
    }
}
