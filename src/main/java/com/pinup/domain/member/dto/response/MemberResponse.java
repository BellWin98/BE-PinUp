package com.pinup.domain.member.dto.response;

import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.Formatter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "유저 정보 응답 DTO")
public class MemberResponse {

    @Schema(description = "유저 ID")
    private Long memberId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "프로필 사진 URL")
    private String profilePictureUrl;

    @Schema(description = "소개글")
    private String bio;

    @Schema(description = "로그인 타입")
    private LoginType loginType;

    @Schema(description = "마케팅 정보 수신 동의")
    private String termsOfMarketing;

    @Schema(description = "작성한 리뷰 수")
    private int reviewCount;

    @Schema(description = "작성 리뷰 평균 별점")
    private double averageStarRating;

    @Schema(description = "핀버디 수")
    private int pinBuddyCount;

    public static MemberResponse from(Member member) {
        List<Review> reviews = member.getReviews();
        List<FriendShip> friends = member.getFriendships();
        double averageStarRating = reviews.stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);
        double roundedAverageRating = Formatter.formatStarRating(averageStarRating);

        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profilePictureUrl(StringUtils.hasText(member.getProfileImageUrl()) ? member.getProfileImageUrl() : "")
                .nickname(StringUtils.hasText(member.getNickname()) ? member.getNickname() : "")
                .bio(StringUtils.hasText(member.getBio()) ? member.getBio() : "")
                .loginType(member.getLoginType())
                .termsOfMarketing(member.getTermsOfMarketing())
                .reviewCount(reviews.size())
                .averageStarRating(roundedAverageRating)
                .pinBuddyCount(friends.size())
                .build();
    }
}
