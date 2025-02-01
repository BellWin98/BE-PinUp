package com.pinup.controller;

import com.pinup.dto.request.ChatRequest;
import com.pinup.dto.request.ChatRoomRequest;
import com.pinup.dto.response.ChatResponse;
import com.pinup.dto.response.ChatRoomResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room")
    public ResponseEntity<ResultResponse> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        ChatRoomResponse result = chatService.createChatRoom(chatRoomRequest);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CREATE_CHAT_ROOM_SUCCESS, result));
    }

    @MessageMapping("/chat/rooms/{roomId}/send")
    @SendTo("/topic/public/rooms/{roodId}")
    public ResponseEntity<ResultResponse> sendMessage(@DestinationVariable Long roomId, @Payload ChatRequest chatRequest) {
        ChatResponse result = chatService.saveChatMessage(chatRequest);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SEND_CHAT_SUCCESS, result));
    }


}
