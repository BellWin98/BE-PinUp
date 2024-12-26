package com.pinup.global.maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinup.dto.response.PlaceResponseByKeyword;
import com.pinup.entity.Member;
import com.pinup.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoMapModule {

    private static final String KAKAO_MAP_API_URI = "https://dapi.kakao.com/v2/local/search";
    private static final String CATEGORY_FORMAT = "/category.json";
    private static final String KEYWORD_FORMAT = "/keyword.json";
    private static final String HEADER_KEY = "Authorization";
    private static final String HEADER_VALUE = "KakaoAK ";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PlaceRepository placeRepository;

    @Value("${kakao.key}")
    private String apiKey;

    public List<PlaceResponseByKeyword> search(Member loginMember, String keyword) {
        URI kakaoSearchUri = buildUri(keyword);

        return executeSearchRequest(kakaoSearchUri, loginMember);
    }

    private URI buildUri(String keyword) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(KAKAO_MAP_API_URI)
                .path(KEYWORD_FORMAT)
                .queryParam("query", keyword);

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

        String kakaoMapId = documentNode.path("id").asText();
        Long reviewCount = placeRepository.getReviewCount(currentMember, kakaoMapId);
        Double averageStarRating = placeRepository.getAverageStarRating(currentMember, kakaoMapId);

        if (averageStarRating != null) {
            averageStarRating = Math.round(averageStarRating * 100) / 100.0;
        } else {
            averageStarRating = 0.0;
        }

        return PlaceResponseByKeyword.builder()
                .kakaoMapId(kakaoMapId)
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

    public List<PlaceResponseByKeyword> search(Member loginMember, String keyword, String latitude,
                                               String longitude, int radius, String sort) {

        URI kakaoSearchUri = buildUri(keyword, latitude, longitude, radius, sort);

        return executeSearchRequest(kakaoSearchUri, loginMember);
    }

    public List<PlaceResponseByKeyword> searchPlaces(Member currentMember, String key, String value,
                                                     String longitude, String latitude, int radius, String sort) {
        String path;
        if (key.equals("category_group_code")) {
            path = CATEGORY_FORMAT;
        } else {
            path = KEYWORD_FORMAT;
        }

        URI uri = buildPlaceSearchUri(path, key, value, longitude, latitude, radius, sort);

        return executeSearchRequest(uri, currentMember);

    }

    private URI buildPlaceSearchUri(String path, String key, String value,
                                    String longitude, String latitude, int radius, String sort) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(KAKAO_MAP_API_URI)
                .path(path)
                .queryParam(key, value)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", radius)
                .queryParam("sort", sort);

        return uriBuilder.encode().build().toUri();
    }

    private URI buildUri(String keyword, String latitude, String longitude, int radius, String sort) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(KAKAO_MAP_API_URI)
                .path(KEYWORD_FORMAT)
                .queryParam("query", keyword)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", radius)
                .queryParam("sort", sort);

        return uriBuilder.encode().build().toUri();
    }
}
