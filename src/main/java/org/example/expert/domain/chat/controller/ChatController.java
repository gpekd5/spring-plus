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

/**
 * /pub 으로 들어온 STOMP 메시지를 채팅방별 /sub 구독 채널로 이어주는 WebSocket 진입점이다.
 */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅 메시지는 영속화된 상태를 기준으로 모든 참여자에게 동일하게 전달되도록
     * DB 저장 후 해당 채팅방 구독자에게 브로드캐스트한다.
     *
     * @param dto 메시지를 보낼 채팅방 식별자, 발신자 이름, 메시지 내용을 담은 값
     */
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
