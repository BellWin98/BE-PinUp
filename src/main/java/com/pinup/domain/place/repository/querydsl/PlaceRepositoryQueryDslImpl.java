package com.pinup.domain.place.repository.querydsl;

import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.place.dto.request.MapBoundDto;
import com.pinup.domain.place.dto.response.MapPlaceResponse;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;
import com.pinup.domain.review.dto.response.ReviewDetailResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.NumberTemplate;
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
    public List<Place> findPlacesByKeywordAndTargetIds(String keyword, List<Long> targetIds, double currLat, double currLng, double radius) {
        BooleanBuilder keywordCond = new BooleanBuilder();
        keywordCond.or(review.content.containsIgnoreCase(keyword));
        NumberTemplate<Double> distance = calculateDistance(currLat, currLng, place.latitude, place.longitude);

        return queryFactory
                .selectDistinct(place)
                .from(place)
                .join(review).on(review.place.eq(place))
                .where(
                        review.member.id.in(targetIds)
                        .and(keywordCond)
                        .and(distance.loe(radius))
                )
                .orderBy(distance.asc())
                .fetch();
    }

    @Override
    public List<MapPlaceResponse> findMapPlacesWithinBounds(Long memberId, String query, PlaceCategory placeCategory, SortType sortType, MapBoundDto mapBound) {
//        int pageSize = pageable.getPageSize();
        Double currLat = mapBound.getCurrLat();
        Double currLng = mapBound.getCurrLng();
        List<Long> targetMemberIds = fetchTargetMemberIds(memberId);
        List<MapPlaceResponse> mapPlaces = queryFactory
                .select(Projections.constructor(MapPlaceResponse.class,
                        place.kakaoPlaceId.as("kakaoPlaceId"),
                        place.name.as("name"),
                        review.starRating.avg().as("averageStarRating"),
                        review.id.countDistinct().as("reviewCount"),
                        calculateDistance(currLat, currLng, place.latitude, place.longitude).as("distance"),
                        place.latitude.as("latitude"),
                        place.longitude.as("longitude"),
                        place.placeCategory.as("placeCategory")
                ))
                .from(place)
                .innerJoin(review).on(place.eq(review.place))
                .where(place.status.eq("Y")
                    .and(review.member.id.in(targetMemberIds))
                    .and(place.latitude.between(mapBound.getSwLat(), mapBound.getNeLat()))
                    .and(place.longitude.between(mapBound.getSwLng(), mapBound.getNeLng()))
                    .and(searchByQuery(query))
                    .and(searchByPlaceCategory(placeCategory))
                )
                .groupBy(place)
                .orderBy(searchBySortType(sortType, currLat, currLng, place.latitude, place.longitude))
//                .offset(pageable.getOffset())
//                .limit(pageSize + 1)
                .fetch();
        for (MapPlaceResponse mapPlace : mapPlaces) {
            String kakaoPlaceId = mapPlace.getKakaoPlaceId();
            mapPlace.setReviewImageUrls(fetchThreeEarliestReviewImageUrls(targetMemberIds, kakaoPlaceId));
            mapPlace.setReviewerProfileImageUrls(fetchThreeLatestReviewerProfileImageUrls(targetMemberIds, kakaoPlaceId));
            mapPlace.setBookmark(isBookmark(kakaoPlaceId, memberId));
        }

/*        boolean hasNext = false;
        if (mapPlaces.size() > pageSize) {
            mapPlaces.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(mapPlaces, pageable, hasNext);*/
        return mapPlaces;
    }

    @Override
    public MapPlaceResponse findMapPlaceDetail(
            Long memberId, String kakaoPlaceId, Double currLat, Double currLon
    ) {
        List<Long> targetMemberIds = fetchTargetMemberIds(memberId);
        MapPlaceResponse mapPlaceDetail = queryFactory
                .select(Projections.constructor(MapPlaceResponse.class,
                        place.kakaoPlaceId.as("kakaoPlaceId"),
                        place.name.as("name"),
                        review.starRating.avg().as("averageStarRating"),
                        review.id.countDistinct().as("reviewCount"),
                        calculateDistance(currLat, currLon, place.latitude, place.longitude).as("distance"),
                        place.latitude.as("latitude"),
                        place.longitude.as("longitude"),
                        place.placeCategory.as("placeCategory")
                ))
                .from(place)
                .innerJoin(review).on(place.eq(review.place))
                .where(place.status.eq("Y")
                        .and(place.kakaoPlaceId.eq(kakaoPlaceId))
                        .and(review.member.id.in(targetMemberIds))
                )
                .fetchOne();
        if (mapPlaceDetail != null) {
            mapPlaceDetail.setReviewImageUrls(fetchThreeEarliestReviewImageUrls(targetMemberIds, kakaoPlaceId));
            mapPlaceDetail.setReviewerProfileImageUrls(fetchThreeLatestReviewerProfileImageUrls(targetMemberIds, kakaoPlaceId));
            mapPlaceDetail.setBookmark(isBookmark(kakaoPlaceId, memberId));
        }
        return mapPlaceDetail;
    }

    @Override
    public List<ReviewDetailResponse> findAllTargetMemberReviews(Long memberId, String kakaoPlaceId) {
        List<Long> targetMemberIds = fetchTargetMemberIds(memberId);
        List<ReviewDetailResponse> reviewDetails = queryFactory
                .selectDistinct(Projections.constructor(ReviewDetailResponse.class,
                        review.id.as("reviewId"),
                        review.member.nickname.as("writerName"),
                        review.member.reviews.size().as("writerTotalReviewCount"),
                        review.starRating,
                        review.createdAt,
                        review.visitedDate,
                        review.content,
                        review.member.profileImageUrl.as("writerProfileImageUrl")
                ))
                .from(review)
                .leftJoin(reviewImage).on(review.eq(reviewImage.review))
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.in(targetMemberIds))
                )
                .fetch();
        for (ReviewDetailResponse reviewDetail : reviewDetails) {
            Long reviewId = reviewDetail.getReviewId();
            reviewDetail.setReviewImageUrls(fetchReviewImageUrls(reviewId));
        }

        return reviewDetails;
    }

    @Override
    public Long getReviewCount(Long memberId, String kakaoPlaceId) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.in(fetchTargetMemberIds(memberId)))
                )
                .fetchOne();
    }

    @Override
    public Double getAverageStarRating(Long memberId, String kakaoPlaceId) {
        return queryFactory
                .select(review.starRating.avg())
                .from(place)
                .join(review).on(place.eq(review.place))
                .where(place.status.eq("Y")
                    .and(place.kakaoPlaceId.eq(kakaoPlaceId))
                    .and(review.member.id.in(fetchTargetMemberIds(memberId)))
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
            SortType sortType, Double currLat, Double currLng,
            NumberPath<Double> placeLat, NumberPath<Double> placeLng
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
                if (currLat == null || currLng == null) {
                    return review.createdAt.desc();
                }
                return calculateDistance(currLat, currLng, placeLat, placeLng).asc();
            }
        }
    }

    private List<String> fetchReviewImageUrls(Long reviewId) {
        return queryFactory
                .select(reviewImage.url)
                .from(reviewImage)
                .where(reviewImage.review.id.eq(reviewId))
                .fetch();
    }

    private List<String> fetchThreeEarliestReviewImageUrls(List<Long> targetMemberUrls, String kakaoPlaceId) {
        return queryFactory
                .selectDistinct(reviewImage.url)
                .from(reviewImage)
                .where(reviewImage.review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(reviewImage.review.member.id.in(targetMemberUrls))
                )
                .orderBy(reviewImage.createdAt.asc())
                .limit(3)
                .fetch();
    }

    private List<String> fetchThreeLatestReviewerProfileImageUrls(List<Long> targetMemberIds, String kakaoPlaceId) {
        return queryFactory
                .selectDistinct(member.profileImageUrl)
                .from(member)
                .innerJoin(review).on(member.eq(review.member))
                .where(review.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(review.member.id.in(targetMemberIds))
                )
                .orderBy(review.updatedAt.desc())
                .limit(3)
                .fetch();
    }

    private NumberTemplate<Double> calculateDistance(
            Double lat1, Double lng1, NumberPath<Double> lat2, NumberPath<Double> lng2
    ) {
        if (lat1 == null || lng1 == null) {
            return Expressions.numberTemplate(Double.class, "NULL");
        }
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                lat1, lat2, lng2, lng1, lat1, lat2
        );
    }

    private List<Long> fetchTargetMemberIds(Long memberId) {
        List<Long> targetMemberIds = new ArrayList<>();
        targetMemberIds.add(memberId);
        targetMemberIds.addAll(fetchPinBuddyIds(memberId));

        return targetMemberIds;
    }

    private List<Long> fetchPinBuddyIds(Long memberId) {
        return queryFactory
                .select(friendShip.friend.id)
                .from(friendShip)
                .where(friendShip.member.id.eq(memberId))
                .fetch();
    }

    private boolean isBookmark(String kakaoPlaceId, Long memberId) {
        BookMark entity = queryFactory
                .selectFrom(bookMark)
                .where(bookMark.place.kakaoPlaceId.eq(kakaoPlaceId)
                        .and(bookMark.member.id.eq(memberId)))
                .fetchOne();

        return entity != null;
    }
}