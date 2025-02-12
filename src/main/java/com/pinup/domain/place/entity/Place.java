package com.pinup.domain.place.entity;

import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kakaoPlaceId; // 카카오맵에서 부여된 장소 ID

    private String name; // 장소명
    private String address; // 주소
    private String roadAddress; // 도로명 주소
    private String defaultImgUrl; // 기본 이미지
    private Double latitude; // 위도(Y)
    private Double longitude; // 경도(X)
    private String status; // 상태

    @Enumerated(EnumType.STRING)
    private PlaceCategory placeCategory;

    @OneToMany(mappedBy = "place", fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Place(String kakaoPlaceId, String name, String address, String roadAddress,
                 Double latitude, Double longitude, PlaceCategory placeCategory) {
        this.kakaoPlaceId = kakaoPlaceId;
        this.name = name;
        this.address = address;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = "Y";
        this.placeCategory = placeCategory;
    }

    public void updateDefaultImgUrl(String defaultImgUrl) {
        this.defaultImgUrl = defaultImgUrl;
    }
}
