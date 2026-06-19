package org.example.expert.domain.manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 할일 담당자 등록, 조회, 삭제 요청을 처리하는 API 진입점이다.
 */
@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    /**
     * 할일 작성자가 다른 사용자를 담당자로 등록할 수 있도록 요청을 위임한다.
     *
     * @param authUser 인증 필터가 주입한 현재 사용자 식별 정보
     * @param todoId 담당자를 등록할 할일 식별자
     * @param managerSaveRequest 담당자로 등록할 사용자 식별자
     * @return 등록된 담당자와 사용자 정보를 담은 응답
     */
    @PostMapping("/todos/{todoId}/managers")
    public ResponseEntity<ManagerSaveResponse> saveManager(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long todoId,
            @Valid @RequestBody ManagerSaveRequest managerSaveRequest
    ) {
        return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequest));
    }

    /**
     * 할일 상세 화면에서 등록된 담당자 목록을 보여주기 위해 조회한다.
     *
     * @param todoId 담당자 목록을 조회할 할일 식별자
     * @return 해당 할일에 등록된 담당자 목록 응답
     */
    @GetMapping("/todos/{todoId}/managers")
    public ResponseEntity<List<ManagerResponse>> getMembers(@PathVariable long todoId) {
        return ResponseEntity.ok(managerService.getManagers(todoId));
    }

    /**
     * 할일 작성자가 해당 할일에 등록된 담당자를 제거할 수 있도록 요청한다.
     *
     * @param authUser 인증 필터가 주입한 현재 사용자 식별 정보
     * @param todoId 담당자를 제거할 할일 식별자
     * @param managerId 제거할 담당자 등록 식별자
     */
    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    public void deleteManager(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long todoId,
            @PathVariable long managerId
    ) {
        managerService.deleteManager(authUser, todoId, managerId);
    }
}
