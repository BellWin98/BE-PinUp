package com.pinup.global.common.image.entity;

import com.pinup.global.common.BaseTimeEntity;
import com.pinup.global.config.s3.S3Service;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // S3 전체 url

    @Column(nullable = false)
    private String imageKey; // S3 객체 키

    @Column(nullable = false)
    private String originalFilename;

    public Image(S3Service.S3FileInfo s3FileInfo) {
        this.imageUrl = s3FileInfo.fileUrl();
        this.imageKey = s3FileInfo.fileKey();
        this.originalFilename = s3FileInfo.originFilename();
    }
}
