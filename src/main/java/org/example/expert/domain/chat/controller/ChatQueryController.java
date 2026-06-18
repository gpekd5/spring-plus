package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.ChatMessageResponse;
import org.example.expert.domain.chat.service.ChatQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatQueryController {

    private final ChatQueryService chatQueryService;

    @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getRecentMessage(size);
    }

    @GetMapping("/messages/before/{id}")
    public List<ChatMessageResponse> getMessagebefore(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getMessageBefore(id,size);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getRecentMessage(roomId,size);
    }
}
