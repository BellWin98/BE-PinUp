package com.pinup.domain.review.dto.response;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {

    private Long id;
    private Long writerId;
    private String writerNickname;
    private String content;
    private Double starRating;
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review) {
        Member writer = review.getMember();
        return ReviewResponseDto.builder()
                .id(review.getId())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
