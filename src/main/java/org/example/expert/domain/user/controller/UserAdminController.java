package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 권한으로 사용자 계정 상태를 변경하는 API 진입점이다.
 */
@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * 운영자가 특정 사용자의 권한을 변경할 때 사용하는 관리자 전용 요청이다.
     *
     * @param userId 권한을 변경할 사용자 식별자
     * @param userRoleChangeRequest 변경할 권한 문자열을 담은 요청 값
     */
    @PatchMapping("/admin/users/{userId}")
    public void changeUserRole(@PathVariable long userId, @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        userAdminService.changeUserRole(userId, userRoleChangeRequest);
    }
}
