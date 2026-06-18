package org.example.expert.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.ChatMessageDto;
import org.example.expert.domain.chat.dto.ChatMessageResponse;
import org.example.expert.domain.chat.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageResponse> getRecentMessage(int size) {
        Pageable pageable = PageRequest.of(0, size);

        return chatMessageRepository.findRecentMessages(pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    public List<ChatMessageResponse> getMessageBefore(Long lastMessageId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        return chatMessageRepository.findMessagesBefore(lastMessageId, pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    public List<ChatMessageResponse> getRecentMessage(Long roomId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        return chatMessageRepository.findRecentByRoom(roomId, pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }
}
