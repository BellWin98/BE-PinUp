package com.pinup.global.config.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinup.domain.place.dto.response.PlaceResponseByKeyword;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoMapModule {

    private static final String KAKAO_MAP_API_URI = "https://dapi.kakao.com/v2/local/search";
    private static final String KEYWORD_FORMAT = "/keyword.json";
    private static final String HEADER_KEY = "Authorization";
    private static final String HEADER_VALUE = "KakaoAK ";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PlaceRepository placeRepository;

    @Value("${kakao.key}")
    private String apiKey;

    public List<PlaceResponseByKeyword> search(Member loginMember, String keyword) {
        URI restaurantSearchUri = buildUri(keyword, PlaceCategory.RESTAURANT.getCode());
        URI cafeSearchUri = buildUri(keyword, PlaceCategory.CAFE.getCode());
        List<PlaceResponseByKeyword> resultOfRestaurant = executeSearchRequest(restaurantSearchUri, loginMember);
        List<PlaceResponseByKeyword> resultOfCafe = executeSearchRequest(cafeSearchUri, loginMember);

        return Stream.of(resultOfRestaurant, resultOfCafe)
                .flatMap(Collection::stream)
                .toList();
    }

    @Cacheable("kakaoSearch")
    public List<PlaceResponseByKeyword> searchParallel(Member member, String keyword) {
        List<String> categories = List.of(PlaceCategory.RESTAURANT.getCode(), PlaceCategory.CAFE.getCode());
        List<List<PlaceResponseByKeyword>> results = categories.parallelStream()
                .map(category -> {
                    URI uri = buildUri(keyword, category);
                    return executeSearchRequest(uri, member);
                })
                .toList();

        return results.stream().flatMap(Collection::stream).toList();
    }

    private URI buildUri(String keyword, String category) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(KAKAO_MAP_API_URI)
                .path(KEYWORD_FORMAT)
                .queryParam("query", keyword)
                .queryParam("category_group_code", category);

        return uriBuilder.encode().build().toUri();
    }

    private List<PlaceResponseByKeyword> executeSearchRequest(URI uri, Member currentMember) {

        List<PlaceResponseByKeyword> placeInfoList = new ArrayList<>();

        try {
            RequestEntity<Void> apiRequest = RequestEntity
                    .get(uri)
                    .header(HEADER_KEY, HEADER_VALUE + apiKey)
                    .build();

            ResponseEntity<String> apiResponse = restTemplate.exchange(apiRequest, String.class);
            JsonNode jsonNode = objectMapper.readTree(apiResponse.getBody());
            JsonNode documentsNode = jsonNode.path("documents");

            for (JsonNode documentNode : documentsNode) {
                PlaceResponseByKeyword placeInfo = extractPlaceInfo(documentNode, currentMember);
                placeInfoList.add(placeInfo);
            }

        } catch (Exception e) {
            log.error("카카오맵 API 요청 간 에러 발생!", e);
        }

        return placeInfoList;
    }

    private PlaceResponseByKeyword extractPlaceInfo(JsonNode documentNode, Member currentMember) {

        String kakaoPlaceId = documentNode.path("id").asText();
        Long reviewCount = placeRepository.getReviewCount(currentMember, kakaoPlaceId);
        Double averageStarRating = placeRepository.getAverageStarRating(currentMember, kakaoPlaceId);

        if (averageStarRating != null) {
            averageStarRating = Math.round(averageStarRating * 10) / 10.0;
        } else {
            averageStarRating = 0.0;
        }

        return PlaceResponseByKeyword.builder()
                .kakaoPlaceId(kakaoPlaceId)
                .name(documentNode.path("place_name").asText())
                .category(documentNode.path("category_group_name").asText())
                .address(documentNode.path("address_name").asText())
                .roadAddress(documentNode.path("road_address_name").asText())
                .latitude(documentNode.path("y").asText())
                .longitude(documentNode.path("x").asText())
                .reviewCount(reviewCount.intValue())
                .averageStarRating(averageStarRating)
                .build();
    }
}
