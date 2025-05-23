package com.pinup.domain.member.controller;

import com.pinup.domain.member.dto.request.LoginRequest;
import com.pinup.domain.member.dto.request.SignUpRequest;
import com.pinup.domain.member.dto.request.SocialLoginRequest;
import com.pinup.domain.member.dto.response.LoginResponse;
import com.pinup.domain.member.service.AuthService;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증(Auth) API", description = "회원가입, 소셜 로그인, 로그아웃, 토큰 재발급")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "앱 회원가입")
    @PostMapping(value = "/sign-up")
    public ResponseEntity<ResultResponse> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {
        authService.signUp(signUpRequest);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SIGN_UP_SUCCESS));
    }

    @Operation(summary = "앱 소셜 로그인", description = "AT, RT, 유저정보 반환")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = LoginResponse.class))})
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> appSocialLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.SOCIAL_LOGIN_SUCCESS,
                authService.appSocialLogin(loginRequest))
        );
    }

    @Operation(summary = "웹 소셜 로그인", description = "AT, RT, 유저정보 반환")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = LoginResponse.class))})
    @PostMapping("/web/social-login")
    public ResponseEntity<ResultResponse> webSocialLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.SOCIAL_LOGIN_SUCCESS,
                authService.webSocialLogin(socialLoginRequest.getProvider(), socialLoginRequest.getCode()))
        );
    }

    @GetMapping("/login/google/callback")
    @Operation(summary = "웹 구글 로그인", description = "AT, RT, 유저정보 반환")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = LoginResponse.class))})
    public ResponseEntity<ResultResponse> googleLogin(@RequestParam("code") String code) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.SOCIAL_LOGIN_SUCCESS, authService.googleLogin(code))
        );
    }

    @Operation(summary = "토큰 재발급 API", description = "헤더에 refreshToken 필요")
    @ApiResponse(content = {@Content(schema = @Schema(implementation = LoginResponse.class))})
    @PostMapping("/refresh")
    public ResponseEntity<ResultResponse> refresh(@RequestHeader("Refresh") String refreshToken) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.TOKEN_REISSUED_SUCCESS, authService.refresh(refreshToken))
        );
    }

    @Operation(summary = "로그아웃 API", description = "헤더에 accessToken 필요")
    @PostMapping("/logout")
    public ResponseEntity<ResultResponse> logout(@RequestHeader("Access") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.LOGOUT_SUCCESS));
    }
}