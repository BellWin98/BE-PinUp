package com.pinup.domain.place.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinup.domain.place.dto.response.PlaceSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SearchCacheRepository {

    private static final String SEARCH_RESULT_PREFIX = "search:result:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 캐시에서 검색 결과 조회
     */
    public Optional<Page<PlaceSearchResponse>> getSearchResults(String cacheKey) {
        String key = SEARCH_RESULT_PREFIX + cacheKey;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            Page<PlaceSearchResponse> results = objectMapper.readValue(value,
                    objectMapper.getTypeFactory().constructParametricType(
                            PageImpl.class,
                            PlaceSearchResponse.class));

            return Optional.of(results);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing cached search results: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
