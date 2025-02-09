package com.pinup.domain.alarm.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {
    private Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(String userEmail, SseEmitter sseEmitter) {
        String key = makeKey(userEmail);
        emitters.put(key, sseEmitter);
        return sseEmitter;
    }

    public void deleteByUserEmail(String userEmail) {
        String key = makeKey(userEmail);
        emitters.remove(key);
    }

    public Map<String, SseEmitter> findAllEmitterStartsWithByUserEmail(String userEmail) {
        String keyPrefix = makeKey(userEmail);
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(keyPrefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String makeKey(String userEmail) {
        return "SSE:USER:" + userEmail + ":";
    }
}