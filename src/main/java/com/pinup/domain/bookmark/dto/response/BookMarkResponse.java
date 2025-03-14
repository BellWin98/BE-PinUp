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

    public BookMarkResponse(Long id, Long placeId, String placeName,
            String placeAddress, String placeRoadAddress, String placeFirstReviewImageUrl,
            Double placeLatitude, Double placeLongitude, String placeStatus,
            PlaceCategory placeCategory, String kakaoPlaceId
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
}