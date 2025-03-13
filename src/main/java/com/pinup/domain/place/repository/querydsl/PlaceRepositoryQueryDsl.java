package com.pinup.domain.place.repository.querydsl;

import com.pinup.domain.place.dto.request.MapBoundDto;
import com.pinup.domain.place.dto.response.MapPlaceResponse;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;
import com.pinup.domain.review.dto.response.ReviewDetailResponse;

import java.util.List;

public interface PlaceRepositoryQueryDsl {
    List<MapPlaceResponse> findMapPlacesWithinBounds(Long memberId, String query, PlaceCategory category, SortType sortType, MapBoundDto mapBound);
    MapPlaceResponse findMapPlaceDetail(Long memberId, String kakaoPlaceId, Double currentLatitude, Double currentLongitude);
    List<ReviewDetailResponse> findAllTargetMemberReviews(Long memberId, String kakaoPlaceId);
    Long getReviewCount(Long memberId, String kakaoPlaceId);
    Double getAverageStarRating(Long memberId, String kakaoPlaceId);
}
