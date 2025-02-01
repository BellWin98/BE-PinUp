package com.pinup.dto.response;

import com.pinup.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Builder
public class ChatRoomResponse {

    private Long id;
    private String name;
    private String createdAt;

    public static ChatRoomResponse from(Room room) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .createdAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(room.getCreatedAt()))
                .build();
    }
}
