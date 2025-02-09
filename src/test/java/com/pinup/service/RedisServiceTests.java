package com.pinup.service;

import com.pinup.global.config.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;

import static com.pinup.constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {RedisService.class})
public class RedisServiceTests {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("Redis에 값을 저장해야 함")
    void testSetValues() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.setValues(TEST_MEMBER_EMAIL, TEST_REFRESH_TOKEN);

        verify(valueOperations, times(1)).set(eq(TEST_MEMBER_EMAIL), eq(TEST_REFRESH_TOKEN), any());
    }

    @Test
    @DisplayName("Redis에서 값을 조회해야 함")
    void testGetValues() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_MEMBER_EMAIL)).thenReturn(TEST_REFRESH_TOKEN);

        String actualValue = redisService.getValues(TEST_MEMBER_EMAIL);

        assertEquals(TEST_REFRESH_TOKEN, actualValue);
    }

    @Test
    @DisplayName("Redis에서 값을 삭제해야 함")
    void testDeleteValues() {
        when(redisTemplate.delete(TEST_MEMBER_EMAIL)).thenReturn(true);

        redisService.deleteValues(TEST_MEMBER_EMAIL);

        verify(redisTemplate, times(1)).delete(TEST_MEMBER_EMAIL);
    }
}