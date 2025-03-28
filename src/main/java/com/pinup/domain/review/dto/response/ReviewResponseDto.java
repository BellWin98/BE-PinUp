package com.pinup.domain.review.dto.response;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewKeyword;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReviewResponseDto {

    private Long id;
    private Long writerId;
    private String writerNickname;
    private String content;
    private Double starRating;
    private LocalDateTime createdAt;
    private List<String> keywords;

    public static ReviewResponseDto from(Review review) {
        Member writer = review.getMember();
        return ReviewResponseDto.builder()
                .id(review.getId())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .createdAt(review.getCreatedAt())
                .keywords(review.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .collect(Collectors.toList()))
                .build();
    }
}
