package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 할일 생성, 단건 조회, 목록 조회, 검색 요청을 처리하는 API 진입점이다.
 */
@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * 인증된 사용자의 할일을 생성하고 생성 시점의 날씨 정보를 함께 저장한다.
     *
     * @param authUser 인증 필터가 주입한 현재 사용자 식별 정보
     * @param todoSaveRequest 생성할 할일의 제목과 내용
     * @return 저장된 할일과 작성자 정보를 담은 응답
     */
    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    /**
     * 목록 화면에서 사용할 할일 페이지를 수정일 기준으로 조회한다.
     *
     * @param page 클라이언트가 요청한 1부터 시작하는 페이지 번호
     * @param size 한 페이지에 포함할 할일 개수
     * @param weather 날씨 기준으로 제한할 선택 필터
     * @param startDate 생성일 범위의 시작일
     * @param endDate 생성일 범위의 종료일
     * @return 조건에 맞는 할일 페이지 응답
     */
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
            ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, startDate, endDate));
    }

    /**
     * 상세 화면에서 작성자 정보와 함께 단일 할일을 조회한다.
     *
     * @param todoId 조회할 할일 식별자
     * @return 할일 상세 정보 응답
     */
    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    /**
     * 복합 검색 조건을 쿼리 파라미터로 받아 할일 목록을 페이지 단위로 검색한다.
     *
     * @param request 검색에 사용할 제목, 작성자 닉네임, 기간 등의 조건
     * @param page 클라이언트가 요청한 1부터 시작하는 페이지 번호
     * @param size 한 페이지에 포함할 검색 결과 개수
     * @return 검색 조건에 맞는 할일 요약 페이지 응답
     */
    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodosList(
            @ModelAttribute TodoSearchRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(todoService.searchTodosList(request,page, size));
    }
}
