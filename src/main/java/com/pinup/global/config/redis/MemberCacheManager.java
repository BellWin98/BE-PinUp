package com.pinup.global.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.ProfileResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberCacheManager {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisMessageListenerContainer listenerContainer;

    private static final String MEMBER_KEY_PREFIX = "member:";
    private static final String PROFILE_KEY_PREFIX = "profile:";
    private static final String LOCK_PREFIX = "lock:";
    private static final String NOTIFICATION_PREFIX = "notify:";

    private static final Duration MEMBER_TTL = Duration.ofHours(1);
    private static final Duration PROFILE_TTL = Duration.ofMinutes(5);
    private static final Duration LOCK_TTL = Duration.ofSeconds(5);
    private static final double TTL_REFRESH_THRESHOLD = 0.1;

    private final Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();
    private RedisScript<Boolean> lockScript;
    private RedisScript<String> getCacheScript;

    @PostConstruct
    public void init() {
        lockScript = new DefaultRedisScript<>(
                "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                        "    redis.call('pexpire', KEYS[1], ARGV[2]) " +
                        "    return true " +
                        "end " +
                        "return false",
                Boolean.class
        );

        getCacheScript = new DefaultRedisScript<>(
                "local value = redis.call('get', KEYS[1]) " +
                        "if value then " +
                        "    local ttl = redis.call('pttl', KEYS[1]) " +
                        "    local maxTtl = tonumber(ARGV[1]) " +
                        "    if ttl > 0 and ttl < (maxTtl * " + TTL_REFRESH_THRESHOLD + ") then " +
                        "        redis.call('pexpire', KEYS[1], maxTtl) " +
                        "    end " +
                        "end " +
                        "return value",
                String.class
        );
    }

    public Optional<MemberResponse> getMemberCache(Long memberId) {
        String key = MEMBER_KEY_PREFIX + memberId;
        return getCache(key, MEMBER_TTL, MemberResponse.class);
    }

    public MemberResponse getMemberWithCache(Long memberId, Supplier<MemberResponse> loader) {
        return getWithCache(MEMBER_KEY_PREFIX + memberId, MEMBER_TTL, loader, MemberResponse.class);
    }

    public Optional<ProfileResponse> getProfileCache(Long memberId) {
        String key = PROFILE_KEY_PREFIX + memberId;
        return getCache(key, PROFILE_TTL, ProfileResponse.class);
    }

    public ProfileResponse getProfileWithCache(Long memberId, Supplier<ProfileResponse> loader) {
        return getWithCache(PROFILE_KEY_PREFIX + memberId, PROFILE_TTL, loader, ProfileResponse.class);
    }

    private <T> Optional<T> getCache(String key, Duration ttl, Class<T> type) {
        String value = redisTemplate.execute(
                getCacheScript,
                Collections.singletonList(key),
                String.valueOf(ttl.toMillis())
        );

        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(value, type));
        } catch (JsonProcessingException e) {
            log.error("캐시 역직렬화 실패. key: {}", key, e);
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    private <T> T getWithCache(String key, Duration ttl, Supplier<T> loader, Class<T> type) {
        Optional<T> cached = getCache(key, ttl, type);
        if (cached.isPresent()) {
            return cached.get();
        }

        String lockKey = LOCK_PREFIX + key;
        String notifyChannel = NOTIFICATION_PREFIX + key;
        String lockValue = java.util.UUID.randomUUID().toString();

        boolean locked = acquireLock(lockKey, lockValue);
        if (locked) {
            try {
                cached = getCache(key, ttl, type);
                if (cached.isPresent()) {
                    return cached.get();
                }

                T value = loader.get();
                putCache(key, value, ttl);

                redisTemplate.convertAndSend(notifyChannel, "ready");

                return value;
            } finally {
                releaseLock(lockKey, lockValue);
            }
        } else {
            return waitForCache(key, notifyChannel, type);
        }
    }

    private <T> T waitForCache(String key, String channel, Class<T> type) {
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(key, future);

        try {
            listenerContainer.addMessageListener(
                    (message, pattern) -> {
                        CompletableFuture<String> pending = pendingRequests.remove(key);
                        if (pending != null) {
                            String cachedValue = redisTemplate.opsForValue().get(key);
                            pending.complete(cachedValue);
                        }
                    },
                    new ChannelTopic(channel)
            );

            String value = future.get(LOCK_TTL.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
            return objectMapper.readValue(value, type);
        } catch (Exception e) {
            log.error("캐시 대기 중 오류 발생. key: {}", key, e);
            pendingRequests.remove(key);
            throw new RuntimeException("캐시 조회 실패", e);
        }
    }

    private boolean acquireLock(String lockKey, String lockValue) {
        return Boolean.TRUE.equals(redisTemplate.execute(
                lockScript,
                Collections.singletonList(lockKey),
                lockValue,
                String.valueOf(LOCK_TTL.toMillis())
        ));
    }

    private void releaseLock(String lockKey, String lockValue) {
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "    return redis.call('del', KEYS[1]) " +
                        "end " +
                        "return false",
                Boolean.class
        );

        redisTemplate.execute(
                script,
                Collections.singletonList(lockKey),
                lockValue
        );
    }

    private <T> void putCache(String key, T value, Duration ttl) {
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, serializedValue, ttl);
        } catch (JsonProcessingException e) {
            log.error("캐시 직렬화 실패. key: {}", key, e);
        }
    }

    public void putMemberCache(Long memberId, MemberResponse response) {
        putCache(MEMBER_KEY_PREFIX + memberId, response, MEMBER_TTL);
    }

    public void putProfileCache(Long memberId, ProfileResponse response) {
        putCache(PROFILE_KEY_PREFIX + memberId, response, PROFILE_TTL);
    }

    public void evictMemberCache(Long memberId) {
        redisTemplate.delete(MEMBER_KEY_PREFIX + memberId);
    }

    public void evictProfileCache(Long memberId) {
        redisTemplate.delete(PROFILE_KEY_PREFIX + memberId);
    }

    public void evictAllCaches(Long memberId) {
        evictMemberCache(memberId);
        evictProfileCache(memberId);
    }
}