package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.ChatMessageResponse;
import org.example.expert.domain.chat.service.ChatQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 입장과 이전 메시지 로딩 시 기존 대화 내역을 HTTP로 조회하기 위한 API를 제공한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatQueryController {

    private final ChatQueryService chatQueryService;

    /**
     * 최초 진입 화면처럼 특정 채팅방 기준이 없는 조회 흐름에서 최근 메시지를 제공한다.
     *
     * @param size 조회할 최근 메시지 개수
     * @return 최근 채팅 메시지 목록
     */
    @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getRecentMessage(size);
    }

    /**
     * 클라이언트가 이미 받은 마지막 메시지보다 이전 대화를 이어서 보여줄 때 사용한다.
     *
     * @param id 기준이 되는 마지막 메시지 식별자
     * @param size 추가로 조회할 이전 메시지 개수
     * @return 기준 메시지보다 오래된 채팅 메시지 목록
     */
    @GetMapping("/messages/before/{id}")
    public List<ChatMessageResponse> getMessagebefore(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getMessageBefore(id,size);
    }

    /**
     * 채팅방 입장 시 해당 방의 기존 대화 내역을 먼저 보여주기 위해 사용한다.
     *
     * @param roomId 대화 내역을 조회할 채팅방 식별자
     * @param size 조회할 최근 메시지 개수
     * @return 해당 채팅방의 최근 채팅 메시지 목록
     */
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getRecentMessage(roomId,size);
    }
}
