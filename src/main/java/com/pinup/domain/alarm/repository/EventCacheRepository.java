package com.pinup.domain.alarm.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EventCacheRepository {
    private Map<String, String> eventCache = new ConcurrentHashMap<>();

    public void save(String userEmail, String eventId, String message) {
        eventCache.put(makeKey(userEmail, eventId), message);
    }

    public Map<String, Object> findAllEventCacheStartsWithByUserEmail(String userEmail) {
        String keyPrefix = makeKey(userEmail, "");
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(keyPrefix))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().substring(keyPrefix.length()),
                        Map.Entry::getValue
                ));
    }

    private String makeKey(String userEmail, String eventId) {
        return "EVENT:USER:" + userEmail + ":" + eventId;
    }
}