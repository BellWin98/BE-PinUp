package com.pinup.domain.article.entity;

import com.pinup.domain.member.entity.Member;
import com.pinup.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    private String imageUrl;

    @Builder
    public Article(String title, Member author, String imageUrl) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
    }

}
