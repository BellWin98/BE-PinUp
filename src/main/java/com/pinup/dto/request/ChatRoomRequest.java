package com.pinup.dto.request;

import com.pinup.entity.Member;
import com.pinup.entity.Room;
import lombok.Data;

@Data
public class ChatRoomRequest {
    private String name;

    public Room toEntity(Member member) {
        return new Room(name, member);
    }
}
