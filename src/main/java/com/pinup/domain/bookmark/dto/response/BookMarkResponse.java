package com.pinup.domain.bookmark.dto.response;

import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.place.entity.PlaceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMarkResponse {
    private Long id;
    private Long placeId;
    private String placeName;
    private String placeAddress;
    private String placeRoadAddress;
    private String placeDefaultImgUrl;
    private Double placeLatitude;
    private Double placeLongitude;
    private String placeStatus;
    private PlaceCategory placeCategory;
    private String kakaoMapId;

    public static BookMarkResponse from(BookMark bookmark) {
        return BookMarkResponse.builder()
                .id(bookmark.getId())
                .placeId(bookmark.getPlace().getId())
                .placeName(bookmark.getPlace().getName())
                .placeAddress(bookmark.getPlace().getAddress())
                .placeRoadAddress(bookmark.getPlace().getRoadAddress())
                .placeDefaultImgUrl(bookmark.getPlace().getDefaultImgUrl())
                .placeLatitude(bookmark.getPlace().getLatitude())
                .placeLongitude(bookmark.getPlace().getLongitude())
                .placeStatus(bookmark.getPlace().getStatus())
                .kakaoMapId(bookmark.getPlace().getKakaoMapId())
                .placeCategory(bookmark.getPlace().getPlaceCategory())
                .build();
    }
}