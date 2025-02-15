package com.pinup.domain.place.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.dto.response.PlaceDetailResponse;
import com.pinup.domain.place.dto.response.PlaceResponseByKeyword;
import com.pinup.domain.place.dto.response.PlaceResponseWithFriendReview;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;
import com.pinup.domain.place.repository.PlaceRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.config.kakao.KakaoMapModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final Logger log = LoggerFactory.getLogger(PlaceService.class);
    private final AuthUtil authUtil;
    private final KakaoMapModule kakaoMapModule;
    private final PlaceRepository placeRepository;

    @Transactional
    public List<PlaceResponseWithFriendReview> getPlaces(
            String query, String category, String sort,
            double swLat, double swLon, double neLat,
            double neLon, double currLat, double currLon
    ) {
        Member loginMember = authUtil.getLoginMember();

        PlaceCategory placeCategory = PlaceCategory.getCategory(category);
        SortType sortType = SortType.getSortType(sort);

        return placeRepository.findAllByMemberAndLocation(
                loginMember, query, placeCategory, sortType,
                swLat, swLon, neLat,
                neLon, currLat, currLon
        );
    }

    @Transactional(readOnly = true)
    public PlaceDetailResponse getPlaceDetail(String kakaoPlaceId, double currentLatitude, double currentLongitude) {
        Member loginMember = authUtil.getLoginMember();
        PlaceDetailResponse placeDetailResponse =
                placeRepository.findByKakaoPlaceIdAndMember(loginMember, kakaoPlaceId, currentLatitude, currentLongitude);
        List<PlaceDetailResponse.ReviewDetailResponse> reviewDetailResponseList = placeDetailResponse.getReviews();
        Map<Integer, Integer> ratingGraph = new HashMap<>();
        for (PlaceDetailResponse.ReviewDetailResponse reviewDetailResponse : reviewDetailResponseList) {
            int range = (int) Math.floor(reviewDetailResponse.getStarRating());
            ratingGraph.put(range, ratingGraph.getOrDefault(range, 0) + 1);
        }
        placeDetailResponse.setRatingGraph(ratingGraph);

        return placeDetailResponse;
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseByKeyword> getPlacesByKeyword(String keyword) {
        Member loginMember = authUtil.getLoginMember();

        return kakaoMapModule.search(loginMember, keyword);
    }
}
