package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    @PostMapping
    public ChatRoom create(@RequestParam String name) {
        ChatRoom room = new ChatRoom(name);
        return chatRoomRepository.save(room);
    }

    @GetMapping
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }
}