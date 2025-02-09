package com.pinup.domain.article.dto.response;

import com.pinup.domain.article.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponse {
    private Long id;
    private String articleTitle;
    private Long authorId;
    private String imageUrl;

    public static ArticleResponse from(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .authorId(article.getAuthor().getId())
                .articleTitle(article.getTitle())
                .imageUrl(article.getImageUrl())
                .build();
    }
}
