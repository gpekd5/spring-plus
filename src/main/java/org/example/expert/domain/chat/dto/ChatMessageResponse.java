package org.example.expert.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.expert.domain.chat.entity.ChatMessage;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private String content;
    private String sender;
    private LocalDateTime createdAt;

    public ChatMessageResponse(ChatMessage message) {
        this.messageId = message.getId();
        this.content = message.getContent();
        this.sender = message.getSender();
        this.createdAt = message.getCreatedAt();
    }
}
