package com.pinup.domain.member.entity;

import com.pinup.global.common.BaseTimeEntity;
import com.pinup.global.common.image.entity.Image;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    public ProfileImage(Image image) {
        this.image = image;
    }

    public void updateImage(Image image) {
        this.image = image;
    }

    public String getImageUrl() {
        return this.image.getImageUrl();
    }
}
