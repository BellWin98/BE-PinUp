package com.pinup.domain.place.repository.querydsl;

import com.pinup.domain.place.dto.response.PlaceDetailResponse;
import com.pinup.domain.place.dto.response.PlaceResponseWithFriendReview;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;

import java.util.List;

public interface PlaceRepositoryQueryDsl {

    List<PlaceResponseWithFriendReview> findAllByMemberAndLocation(
            Member loginMember,
            String query,
            PlaceCategory category,
            SortType sortType,
            double swLatitude,
            double swLongitude,
            double neLatitude,
            double neLongitude,
            double currentLatitude,
            double currentLongitude
    );

    PlaceDetailResponse findByKakaoPlaceIdAndMember(
            Member loginMember,
            String kakaoPlaceId,
            double currentLatitude,
            double currentLongitude
    );

    Long getReviewCount(Member loginMember, String kakaoMapId);
    Double getAverageStarRating(Member loginMember, String kakaoMapId);
}
