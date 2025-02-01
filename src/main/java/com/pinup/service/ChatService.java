package com.pinup.service;

import com.pinup.dto.request.ChatRequest;
import com.pinup.dto.request.ChatRoomRequest;
import com.pinup.dto.response.ChatResponse;
import com.pinup.dto.response.ChatRoomResponse;
import com.pinup.dto.response.MemberResponse;
import com.pinup.entity.Chat;
import com.pinup.entity.Member;
import com.pinup.entity.Room;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.exception.ErrorCode;
import com.pinup.global.util.AuthUtil;
import com.pinup.repository.ChatRepository;
import com.pinup.repository.ChatRoomRepository;
import com.pinup.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations template;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AuthUtil authUtil;

    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest) {
        Member loginMember = authUtil.getLoginMember();
        Room createdRoom = chatRoomRequest.toEntity(loginMember);

        return ChatRoomResponse.from(chatRoomRepository.save(createdRoom));
    }

    @Transactional
    public ChatResponse saveChatMessage(ChatRequest chatRequest) {
        Room room = chatRoomRepository.findById(chatRequest.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INPUT_VALUE_INVALID));
        Member member = memberRepository.findByNickname(chatRequest.getSender())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Chat createdChat = chatRequest.toEntity(room, member);
        ChatResponse chatResponse = ChatResponse.from(chatRepository.save(createdChat));
        template.convertAndSend("/sub/chatroom/" + chatRequest.getRoomId(), chatResponse);

        return chatResponse;
    }
}
