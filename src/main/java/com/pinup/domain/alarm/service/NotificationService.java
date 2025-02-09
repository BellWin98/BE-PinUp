package com.pinup.domain.alarm.service;

import com.pinup.domain.alarm.exception.SseConnectionErrorException;
import com.pinup.domain.alarm.repository.EmitterRepository;
import com.pinup.domain.alarm.repository.EventCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final EventCacheRepository eventCacheRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(String lastEventId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(userEmail, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteByUserEmail(userEmail));
        emitter.onTimeout(() -> emitterRepository.deleteByUserEmail(userEmail));

        // 503 에러 방지를 위한 더미 이벤트 전송
        sendToClient(emitter, makeEventId(userEmail), "EventStream Created. [userEmail=" + userEmail + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = eventCacheRepository.findAllEventCacheStartsWithByUserEmail(userEmail);
            events.entrySet().stream()
                    .filter(entry -> entry.getKey().compareTo(lastEventId) > 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void sendNotification(String userEmail, String message) {
        String eventId = makeEventId(userEmail);

        eventCacheRepository.save(userEmail, eventId, message);

        emitterRepository.findAllEmitterStartsWithByUserEmail(userEmail)
                .forEach((key, emitter) -> {
                    sendToClient(emitter, eventId, message);
                });
    }

    private String makeEventId(String userEmail) {
        return userEmail + "_" + System.currentTimeMillis();
    }

    private void sendToClient(SseEmitter emitter, String eventId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteByUserEmail(eventId);
            throw new SseConnectionErrorException();
        }
    }
}
