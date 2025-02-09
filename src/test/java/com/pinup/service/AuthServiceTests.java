package com.pinup.service;

import com.pinup.domain.member.service.AuthService;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.dto.response.LoginResponse;
import com.pinup.global.config.jwt.JwtTokenProvider;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.config.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;


import static com.pinup.constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisService redisService;

    @Test
    @DisplayName("토큰 갱신 시 새로운 토큰을 반환해야 함")
    void testRefresh() {
        when(jwtTokenProvider.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getEmail(TEST_REFRESH_TOKEN)).thenReturn(TEST_MEMBER_EMAIL);
        when(redisService.getValues(any())).thenReturn(TEST_REFRESH_TOKEN);
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(Member.builder().build()));
        when(jwtTokenProvider.createAccessToken(any(), any())).thenReturn(TEST_NEW_ACCESS_TOKEN);
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn(TEST_NEW_REFRESH_TOKEN);

        LoginResponse response = authService.refresh(TEST_REFRESH_TOKEN);

        assertNotNull(response);
        assertEquals(TEST_NEW_ACCESS_TOKEN, response.getAccessToken());
        assertEquals(TEST_NEW_REFRESH_TOKEN, response.getRefreshToken());
        verify(redisService, times(1)).setValues(any(), any());
    }

    @Test
    @DisplayName("로그아웃 시 리프레시 토큰을 삭제해야 함")
    void testLogout() {
        when(jwtTokenProvider.validateToken(TEST_ACCESS_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getEmail(TEST_ACCESS_TOKEN)).thenReturn(TEST_MEMBER_EMAIL);

        authService.logout(TEST_ACCESS_TOKEN);

        verify(redisService, times(1)).deleteValues(TEST_MEMBER_EMAIL);
    }
}