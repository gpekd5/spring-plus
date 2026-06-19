package org.example.expert.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.ChatMessageDto;
import org.example.expert.domain.chat.dto.ChatMessageResponse;
import org.example.expert.domain.chat.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 채팅방 입장 시 기존 대화 내역을 먼저 보여주기 위한 HTTP 이전 메시지 조회 흐름을 담당한다.
 */
@Service
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatMessageRepository chatMessageRepository;

    /**
     * 방 구분 없이 최신 대화 흐름을 보여주는 화면을 위해 최근 메시지만 제한해 가져온다.
     *
     * @param size 조회할 최근 메시지 개수
     * @return 최근 채팅 메시지 목록
     */
    public List<ChatMessageResponse> getRecentMessage(int size) {
        Pageable pageable = PageRequest.of(0, size);

        return chatMessageRepository.findRecentMessages(pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    /**
     * 이전 메시지 추가 로딩에서 중복 노출을 피하기 위해 마지막으로 받은 메시지 ID를 기준으로 조회한다.
     *
     * @param lastMessageId 클라이언트가 마지막으로 받은 메시지 식별자
     * @param size 추가로 조회할 이전 메시지 개수
     * @return 기준 메시지보다 오래된 채팅 메시지 목록
     */
    public List<ChatMessageResponse> getMessageBefore(Long lastMessageId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        return chatMessageRepository.findMessagesBefore(lastMessageId, pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    /**
     * 채팅방 입장 시 해당 방의 기존 대화 내역만 먼저 보여주기 위해 방 ID로 범위를 제한한다.
     *
     * @param roomId 대화 내역을 조회할 채팅방 식별자
     * @param size 조회할 최근 메시지 개수
     * @return 해당 채팅방의 최근 채팅 메시지 목록
     */
    public List<ChatMessageResponse> getRecentMessage(Long roomId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        return chatMessageRepository.findRecentByRoom(roomId, pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }
}
