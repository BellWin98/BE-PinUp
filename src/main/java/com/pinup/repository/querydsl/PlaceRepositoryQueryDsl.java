package com.pinup.repository.querydsl;

import com.pinup.dto.response.PlaceDetailResponse;
import com.pinup.dto.response.PlaceResponseWithFriendReview;
import com.pinup.entity.Member;
import com.pinup.enums.PlaceCategory;
import com.pinup.enums.SortType;

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
