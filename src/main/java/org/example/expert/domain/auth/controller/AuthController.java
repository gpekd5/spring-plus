package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증이 필요 없는 회원가입과 로그인 요청을 처리하는 진입점이다.
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 신규 사용자 가입 후 클라이언트가 바로 인증 상태로 전환할 수 있도록 토큰을 발급한다.
     *
     * @param signupRequest 가입에 필요한 이메일, 비밀번호, 권한, 닉네임 정보
     * @return 가입된 사용자가 이후 요청에 사용할 인증 토큰 응답
     */
    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    /**
     * 이메일과 비밀번호 검증에 성공한 사용자에게 API 인증용 토큰을 발급한다.
     *
     * @param signinRequest 로그인 검증에 사용할 이메일과 비밀번호 정보
     * @return 로그인된 사용자가 이후 요청에 사용할 인증 토큰 응답
     */
    @PostMapping("/auth/signin")
    public SigninResponse signin(@Valid @RequestBody SigninRequest signinRequest) {
        return authService.signin(signinRequest);
    }
}
