package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 일반 사용자 조회와 본인 계정 변경 요청을 처리하는 API 진입점이다.
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 다른 도메인에서 사용자 식별 정보를 확인할 수 있도록 단일 사용자 정보를 조회한다.
     *
     * @param userId 조회 대상 사용자의 식별자
     * @return 사용자 식별자와 이메일을 담은 응답
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 현재 로그인한 사용자 기준으로 비밀번호 변경을 요청한다.
     *
     * @param authUser 인증 필터가 주입한 현재 사용자 식별 정보
     * @param userChangePasswordRequest 기존 비밀번호와 새 비밀번호 값
     */
    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }
}
