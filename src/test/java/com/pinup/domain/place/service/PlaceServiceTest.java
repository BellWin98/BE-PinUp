package com.pinup.domain.place.service;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.dto.request.MapBoundDto;
import com.pinup.domain.place.dto.response.MapPlaceResponse;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.repository.PlaceRepository;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.config.kakao.KakaoMapModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private KakaoMapModule kakaoMapModule;

    @Mock
    private PlaceClusteringService placeClusteringService;

    @InjectMocks
    private PlaceService placeService;

    private Member member;
    private Place place;
    private Review review;

    @BeforeEach
    void setUp() {
        member = createMockMember();
        place = createMockPlace();
        review = createMockReview();
        review.attachMember(member);
        review.attachPlace(place);
    }

    @Test
    public void testGetMapPlaces_WithValidParameters_ReturnsPlaceList() {

        // Given
        MapBoundDto mapBound = MapBoundDto.builder()
                .neLat(37.6406328)
                .neLng(127.0695001)
                .swLat(37.536981)
                .swLng(126.9943125)
                .currLat(37.570421)
                .currLng(126.808866)
                .build();

        when(authUtil.getLoginMember()).thenReturn(member);

        // When
        List<MapPlaceResponse> result = placeService.getMapPlaces("", "ALL", "NEAR", mapBound);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).getKakaoPlaceId());
    }

    private Member createMockMember() {
        return Member.builder()
                .email("test@example.com")
                .name("test_name")
                .nickname("test_nickname")
                .profileImageUrl("http://example.com/image.jpg")
                .socialId("test_socialId")
                .termsOfMarketing("N")
                .loginType(LoginType.GOOGLE)
                .build();
    }

    private Place createMockPlace() {
        return Place.builder()
                .name("test_place")
                .address("test_address")
                .kakaoPlaceId("test_kakako_place_id")
                .latitude(37.6095282251213)
                .longitude(127.02729564873)
                .placeCategory(PlaceCategory.RESTAURANT)
                .roadAddress("test_road_address")
                .build();
    }

    private Review createMockReview() {
        return Review.builder()
                .content("test_content")
                .starRating(3.5)
                .visitedDate("25.03.19")
                .build();
    }
}