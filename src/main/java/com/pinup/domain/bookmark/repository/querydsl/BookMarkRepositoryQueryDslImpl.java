package com.pinup.domain.bookmark.repository.querydsl;

import com.pinup.domain.bookmark.dto.response.BookMarkResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.pinup.domain.bookmark.entity.QBookMark.bookMark;
import static com.pinup.domain.place.entity.QPlace.place;
import static com.pinup.domain.review.entity.QReview.review;
import static com.pinup.domain.review.entity.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class BookMarkRepositoryQueryDslImpl implements BookMarkRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BookMarkResponse> findAllByMemberAndFilter(
            Member member,
            PlaceCategory placeCategory,
            SortType sortType,
            double currentLatitude,
            double currentLongitude
    ) {
        StringTemplate firstReviewImageUrl = Expressions.stringTemplate(
                "COALESCE((SELECT ri.url FROM ReviewImage ri " +
                        "JOIN ri.review r " +
                        "WHERE r.place = {0} " +
                        "ORDER BY ri.createdAt ASC LIMIT 1), {1})",
                place,
                place.defaultImgUrl
        );

        return queryFactory
                .select(Projections.constructor(BookMarkResponse.class,
                        bookMark.id,
                        place.id,
                        place.name,
                        place.address,
                        place.roadAddress,
                        firstReviewImageUrl,
                        place.latitude,
                        place.longitude,
                        place.status,
                        place.placeCategory,
                        place.kakaoPlaceId
                ))
                .from(bookMark)
                .join(bookMark.place, place)
                .leftJoin(review).on(review.place.eq(place))
                .where(
                        bookMark.member.eq(member)
                                .and(place.status.eq("Y"))
                                .and(searchByPlaceCategory(placeCategory))
                )
                .groupBy(bookMark.id, place)
                .orderBy(searchBySortType(sortType, currentLatitude, currentLongitude, place.latitude, place.longitude))
                .fetch();
    }

    private BooleanExpression searchByPlaceCategory(PlaceCategory placeCategory) {
        return placeCategory != null && !placeCategory.equals(PlaceCategory.ALL) ?
                place.placeCategory.eq(placeCategory) : null;
    }

    private OrderSpecifier<?> searchBySortType(
            SortType sortType,
            double currLat,
            double currLon,
            NumberPath<Double> placeLat,
            NumberPath<Double> placeLon
    ) {
        if (sortType == null) {
            return calculateDistance(currLat, currLon, placeLat, placeLon).asc();
        }

        return switch (sortType) {
            case LATEST -> bookMark.createdAt.desc();
            case STAR_HIGH -> review.starRating.avg().desc();
            case STAR_LOW -> review.starRating.avg().asc();
            default -> calculateDistance(currLat, currLon, placeLat, placeLon).asc();
        };
    }

    private NumberTemplate<Double> calculateDistance(
            double latitude1,
            double longitude1,
            NumberPath<Double> latitude2,
            NumberPath<Double> longitude2
    ) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5})))",
                latitude1, latitude2, longitude2, longitude1, latitude1, latitude2
        );
    }
}