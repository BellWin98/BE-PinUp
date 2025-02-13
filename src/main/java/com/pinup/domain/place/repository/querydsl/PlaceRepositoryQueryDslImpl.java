package com.pinup.domain.place.repository.querydsl;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.dto.response.PlaceDetailResponse;
import com.pinup.domain.place.dto.response.PlaceResponse;
import com.pinup.domain.place.dto.response.PlaceResponseWithFriendReview;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.pinup.domain.bookmark.entity.QBookMark.bookMark;
import static com.pinup.domain.friend.entity.QFriendShip.friendShip;
import static com.pinup.domain.member.entity.QMember.member;
import static com.pinup.domain.place.entity.QPlace.place;
import static com.pinup.domain.review.entity.QReview.review;
import static com.pinup.domain.review.entity.QReviewImage.reviewImage;

@RequiredArgsConstructor
@Slf4j
public class PlaceRepositoryQueryDslImpl implements PlaceRepositoryQueryDsl{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Place> findAllByMemberAndLocation(
            List<Long> allowedMemberIds, String query, PlaceCategory placeCategory, SortType sortType,
            double swLat, double swLon, double neLat, double neLon, double currLat, double currLon
    ) {

        return queryFactory
                .selectFrom(place)
                .innerJoin(review).on(place.eq(review.place))
                .where(place.status.eq("Y")
                        .and(review.member.id.in(allowedMemberIds))
                        .and(place.latitude.between(swLat, neLat))
                        .and(place.longitude.between(swLon, neLon))
                        .and(searchByQuery(query))
                        .and(searchByPlaceCategory(placeCategory))
                )
                .orderBy(searchBySortType(sortType, currLat, currLon, place.latitude, place.longitude))
                .fetch();
    }

    @Override
    public PlaceDetailResponse findByKakaoPlaceIdAndMember(
            Member loginMember, String kakaoPlaceId, double currentLatitude, double currentLongitude
    ) {
        List<PlaceDetailResponse.ReviewDetailResponse> reviewDetailList = queryFactory
                .select(Projections.constructor(
                        PlaceDetailResponse.ReviewDetailResponse.class,
                        review.id.as("reviewId"),
                        review.member.name.as("writerName"),
                        review.member.reviews.size().as("writerTotalReviewCount"),
                        review.starRating.as("starRating"),
                        review.visitedDate.as("visitedDate"),
                        review.content.as("content"),
                        review.member.profileImageUrl.as("writerProfileImageUrl")
                ))
                .from(review)
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.eq(loginMember.getId())
                                .or(review.member.id.in(fetchPinBuddyIds(loginMember.getId())))
                        )
                )
                .orderBy(review.createdAt.desc())
                .fetch();

        for (PlaceDetailResponse.ReviewDetailResponse reviewDetailResponse : reviewDetailList) {

            Long reviewId = reviewDetailResponse.getReviewId();

            List<String> reviewImageUrls = queryFactory
                    .select(reviewImage.url)
                    .from(reviewImage)
                    .where(reviewImage.review.id.eq(reviewId))
                    .fetch();

            reviewDetailResponse.setReviewImageUrls(reviewImageUrls);
        }

        PlaceDetailResponse placeDetailResponse = queryFactory
                .select(Projections.constructor(
                                PlaceDetailResponse.class,
                                place.name.as("placeName"),
                                review.countDistinct().as("reviewCount"),
                                review.starRating.avg().as("averageStarRating"),
                                calculateDistance(currentLatitude, currentLongitude, place.latitude, place.longitude).as("distance"),
                                place.latitude.as("latitude"),
                                place.longitude.as("longitude"),
                                place.placeCategory.as("placeCategory"),
                                isBookmark(kakaoPlaceId, loginMember.getId()).as("isBookmark")
                        )
                )
                .from(place)
                .leftJoin(review).on(place.eq(review.place))
                .where(place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.eq(loginMember.getId())
                                .or(review.member.id.in(fetchPinBuddyIds(loginMember.getId())))
                        )
                )
                .fetchOne();

        if (placeDetailResponse != null) {
            placeDetailResponse.setReviewImageUrls(fetchReviewImageUrls(kakaoPlaceId, loginMember.getId()));
            placeDetailResponse.setReviewerProfileImageUrls(fetchReviewerProfileImageUrls(kakaoPlaceId, loginMember.getId()));
            placeDetailResponse.setReviews(reviewDetailList);
        } else {
            throw new EntityNotFoundException(ErrorCode.PLACE_NOT_FOUND);
        }

        return placeDetailResponse;
    }

    @Override
    public Long getReviewCount(Member loginMember, String kakaoPlaceId) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.eq(loginMember.getId())
                                .or(review.member.id.in(fetchPinBuddyIds(loginMember.getId())))
                        )
                )
                .fetchOne();
    }

    @Override
    public Double getAverageStarRating(Member loginMember, String kakaoPlaceId) {
        return queryFactory
                .select(review.starRating.avg())
                .from(place)
                .join(review).on(place.eq(review.place))
                .where(place.status.eq("Y")
                        .and(place.kakaoPlaceId.eq(kakaoPlaceId))
                        .and(review.member.id.eq(loginMember.getId())
                                .or(review.member.id.in(fetchPinBuddyIds(loginMember.getId())))
                        )

                )
                .fetchOne();
    }

    private BooleanExpression searchByQuery(String query) {
        return !query.isEmpty() ? place.name.containsIgnoreCase(query) : null;
    }

    private BooleanExpression searchByPlaceCategory(PlaceCategory placeCategory) {
        return !placeCategory.equals(PlaceCategory.ALL) ? place.placeCategory.eq(placeCategory) : null;
    }

    private OrderSpecifier<?> searchBySortType(
            SortType sortType, double currLat, double currLon,
            NumberPath<Double> placeLat, NumberPath<Double> placeLon

    ) {
        switch (sortType) {
            case LATEST -> {
                return review.createdAt.desc();
            }
            case STAR_HIGH -> {
                return review.starRating.avg().desc();
            }
            case STAR_LOW -> {
                return review.starRating.avg().asc();
            }
            default -> {
                return calculateDistance(currLat, currLon, placeLat, placeLon).asc();
            }
        }
    }

    private void addImageInfoOnPlaceResult(List<PlaceResponseWithFriendReview> result, Member loginMember) {
        for (PlaceResponseWithFriendReview response : result) {
            String kakaoPlaceId = response.getKakaoPlaceId();
            response.setReviewImageUrls(fetchReviewImageUrls(kakaoPlaceId, loginMember.getId()));
            response.setReviewerProfileImageUrls(fetchReviewerProfileImageUrls(kakaoPlaceId, loginMember.getId()));
        }
    }

    private List<String> fetchReviewImageUrls(String kakaoPlaceId, Long loginMemberId) {
        return queryFactory
                .select(reviewImage.url)
                .from(reviewImage)
                .join(review).on(reviewImage.review.eq(review))
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.eq(loginMemberId)
                                .or(review.member.id.in(fetchPinBuddyIds(loginMemberId))))
                        .and(reviewImage.url.isNotNull()))
                .orderBy(reviewImage.createdAt.asc())
                .limit(3)
                .fetch();
    }

    private List<String> fetchReviewerProfileImageUrls(String kakaoPlaceId, Long loginMemberId) {
        return queryFactory
                .selectDistinct(member.profileImageUrl)
                .from(member)
                .join(review).on(member.eq(review.member))
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.eq(loginMemberId)
                                .or(review.member.id.in(fetchPinBuddyIds(loginMemberId)))))
                .orderBy(review.updatedAt.desc())
                .limit(3)
                .fetch();
    }

    private NumberTemplate<Double> calculateDistance(
            double latitude1, double longitude1, NumberPath<Double> latitude2, NumberPath<Double> longitude2
    ) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                latitude1, latitude2, longitude2, longitude1, latitude1, latitude2
        );
    }

    private List<Long> fetchPinBuddyIds(Long memberId) {
        return queryFactory
                .select(friendShip.friend.id)
                .from(friendShip)
                .where(friendShip.member.id.eq(memberId))
                .fetch();
    }

    private BooleanExpression isBookmark(String kakaoPlaceId, Long memberId) {

        return JPAExpressions
                .selectOne()
                .from(bookMark)
                .where(bookMark.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(bookMark.member.id.eq(memberId)))
                .exists();
    }
}