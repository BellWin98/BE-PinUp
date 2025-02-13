package com.pinup.domain.bookmark.dto.response;
import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.place.entity.PlaceCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class BookMarkResponse {
    private Long id;
    private Long placeId;
    private String placeName;
    private String placeAddress;
    private String placeRoadAddress;
    private String placeFirstReviewImageUrl;
    private Double placeLatitude;
    private Double placeLongitude;
    private String placeStatus;
    private PlaceCategory placeCategory;
    private String kakaoPlaceId;

    public BookMarkResponse(
            Long id,
            Long placeId,
            String placeName,
            String placeAddress,
            String placeRoadAddress,
            String placeFirstReviewImageUrl,
            Double placeLatitude,
            Double placeLongitude,
            String placeStatus,
            PlaceCategory placeCategory,
            String kakaoPlaceId
    ) {
        this.id = id;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeRoadAddress = placeRoadAddress;
        this.placeFirstReviewImageUrl = placeFirstReviewImageUrl;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.placeStatus = placeStatus;
        this.placeCategory = placeCategory;
        this.kakaoPlaceId = kakaoPlaceId;
    }

    public static BookMarkResponse from(BookMark bookmark) {
        String firstReviewImageUrl = bookmark.getPlace().getReviews().stream()
                .filter(review -> !review.getReviewImages().isEmpty())
                .findFirst()
                .map(review -> review.getReviewImages().get(0).getUrl())
                .orElse(bookmark.getPlace().getDefaultImgUrl());

        return BookMarkResponse.builder()
                .id(bookmark.getId())
                .placeId(bookmark.getPlace().getId())
                .placeName(bookmark.getPlace().getName())
                .placeAddress(bookmark.getPlace().getAddress())
                .placeRoadAddress(bookmark.getPlace().getRoadAddress())
                .placeFirstReviewImageUrl(firstReviewImageUrl)
                .placeLatitude(bookmark.getPlace().getLatitude())
                .placeLongitude(bookmark.getPlace().getLongitude())
                .placeStatus(bookmark.getPlace().getStatus())
                .kakaoPlaceId(bookmark.getPlace().getKakaoPlaceId())
                .placeCategory(bookmark.getPlace().getPlaceCategory())
                .build();
    }
}