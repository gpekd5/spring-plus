package org.example.expert.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 할일에 종속된 댓글 생성과 조회 요청을 처리하는 API 진입점이다.
 */
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 인증된 사용자가 특정 할일에 댓글을 남길 수 있도록 요청을 위임한다.
     *
     * @param authUser 인증 필터가 주입한 현재 사용자 식별 정보
     * @param todoId 댓글을 등록할 할일 식별자
     * @param commentSaveRequest 저장할 댓글 내용
     * @return 저장된 댓글과 작성자 정보를 담은 응답
     */
    @PostMapping("/todos/{todoId}/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long todoId,
            @Valid @RequestBody CommentSaveRequest commentSaveRequest
    ) {
        return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentSaveRequest));
    }

    /**
     * 할일 상세 화면에서 댓글 작성자 정보를 함께 보여주기 위해 댓글 목록을 조회한다.
     *
     * @param todoId 댓글을 조회할 할일 식별자
     * @return 해당 할일에 등록된 댓글 목록 응답
     */
    @GetMapping("/todos/{todoId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable long todoId) {
        return ResponseEntity.ok(commentService.getComments(todoId));
    }
}
