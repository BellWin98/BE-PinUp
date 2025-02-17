package com.pinup.domain.member.controller;


import com.pinup.domain.member.dto.request.LoginRequest;
import com.pinup.domain.member.dto.request.NormalLoginRequest;
import com.pinup.domain.member.dto.request.MemberJoinRequest;
import com.pinup.domain.member.dto.request.SignUpRequest;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.domain.member.dto.response.LoginResponse;
import com.pinup.domain.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증(Auth) API", description = "소셜 로그인, 로그아웃, 토큰 재발급")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "회원가입 API", description = "SocialId 반환")
    @PostMapping(value = "/sign-up", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> signUp(
            @RequestPart SignUpRequest signUpRequest,
            @RequestPart(name = "multipartFile", required = false) MultipartFile multipartFile
    ) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.SIGN_UP_SUCCESS,
                authService.signUp(signUpRequest, multipartFile))
        );
    }

    @Operation(summary = "로그인 API", description = "AT, RT, 유저정보 반환")
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ResultResponse.of(
                ResultCode.SOCIAL_LOGIN_SUCCESS,
                authService.login(loginRequest))
        );
    }

    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        String authorizationUrl = authService.getGoogleAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/login/google/callback")
    @Operation(summary = "소셜 로그인 API", description = "토큰 및 유저 기본 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "소셜 로그인에 성공하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = LoginResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> googleCallback(@RequestParam("code") String code) {
        LoginResponse loginResponse = authService.googleLogin(code);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SOCIAL_LOGIN_SUCCESS, loginResponse));
    }

    @GetMapping("/tokens")
    public ResponseEntity<ResultResponse> getTokens(HttpServletRequest request) {
        LoginResponse loginResponse = authService.getTokens(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.TOKEN_ISSUED_SUCCESS, loginResponse));

    }

    @PostMapping("/refresh")
    public ResponseEntity<ResultResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        LoginResponse loginResponse = authService.refresh(refreshToken);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.TOKEN_REISSUED_SUCCESS, loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultResponse> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.LOGOUT_SUCCESS));
    }

    @PostMapping("/join")
    public ResponseEntity<ResultResponse> memberJoin(@RequestBody MemberJoinRequest request) {
        authService.join(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SIGN_UP_SUCCESS));
    }

    @PostMapping("/login/normal")
    public ResponseEntity<ResultResponse> normalLogin(@RequestBody NormalLoginRequest request) {
        LoginResponse loginResponse = authService.normalLogin(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.NORMAL_LOGIN_SUCCESS, loginResponse));
    }
}