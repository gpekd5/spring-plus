package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.ChatMessageDto;
import org.example.expert.domain.chat.dto.ChatMessageResponse;
import org.example.expert.domain.chat.entity.ChatMessage;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatMessageRepository;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void send(ChatMessageDto dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getRoomId()).orElseThrow(
                () -> new IllegalArgumentException("채팅방을 찾을 수 없습니다.")
        );

        ChatMessage message = new ChatMessage(chatRoom, dto.getSender(), dto.getContent());
        ChatMessage savedMessage = chatMessageRepository.save(message);

        ChatMessageResponse response = new ChatMessageResponse(savedMessage);

        messagingTemplate.convertAndSend("/sub/chat/" + dto.getRoomId(), response);

    }
}
