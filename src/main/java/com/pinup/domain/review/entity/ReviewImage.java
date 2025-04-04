package com.pinup.domain.review.entity;

import com.pinup.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String imageKey; // S3에 저장된 이미지의 키(경로)

    @Column(length = 100)
    private String originalFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public ReviewImage(String url) {
        this.url = url;
    }

    public ReviewImage(String url, String imageKey, String originalFilename) {
        this.url = url;
        this.imageKey = imageKey;
        this.originalFilename = originalFilename;
    }

    public void attachReview(Review review){
        this.review = review;
        review.getReviewImages().add(this);
    }

    public void updateImage(String url, String imageKey, String originalFilename) {
        this.url = url;
        this.imageKey = imageKey;
        this.originalFilename = originalFilename;
    }
}
