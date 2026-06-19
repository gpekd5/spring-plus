package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 익명 채팅 사용자가 인증 절차 없이 방을 만들고 선택할 수 있도록 열어둔 채팅방 API다.
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 익명 사용자가 대화를 시작할 수 있도록 채팅방을 생성한다.
     *
     * @param name 생성할 채팅방 이름
     * @return 저장된 채팅방 정보
     */
    @PostMapping
    public ChatRoom create(@RequestParam String name) {
        ChatRoom room = new ChatRoom(name);
        return chatRoomRepository.save(room);
    }

    /**
     * 클라이언트가 입장 가능한 채팅방을 선택할 수 있도록 전체 채팅방을 제공한다.
     *
     * @return 등록된 전체 채팅방 목록
     */
    @GetMapping
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }
}