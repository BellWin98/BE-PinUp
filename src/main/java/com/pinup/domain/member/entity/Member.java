package com.pinup.domain.member.entity;

import com.pinup.domain.alarm.entity.Alarm;
import com.pinup.domain.article.entity.Article;
import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(100)", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "VARCHAR(10)", unique = true)
    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "VARCHAR(1)")
    private String status;

    @Column(columnDefinition = "VARCHAR(100)")
    private String bio;

    private LocalDateTime lastNicknameUpdateDate;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String socialId;

    private String password;

    @Column(columnDefinition = "VARCHAR(1)")
    private final String termsOfAgreement = "Y";

    @Column(columnDefinition = "VARCHAR(1)")
    private final String termsOfPrivacy = "Y";

    @Column(columnDefinition = "VARCHAR(1)")
    private final String termsOfGeolocation = "Y";

    @Column(columnDefinition = "VARCHAR(1)")
    private String termsOfMarketing;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<FriendShip> friendships = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Article> editorArticles = new ArrayList<>();

    @Builder
    public Member(String email, String name, String profileImageUrl,
                  LoginType loginType, String socialId, String password) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.loginType = loginType;
        this.socialId = socialId;
        this.role = Role.ROLE_USER;
        this.status = "Y";
        this.password = password;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.lastNicknameUpdateDate = LocalDateTime.now();
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateTermsOfMarketing(String termsOfMarketing) {
        this.termsOfMarketing = termsOfMarketing;
    }

    public boolean canUpdateNickname() {
        return this.lastNicknameUpdateDate == null || LocalDateTime.now().isAfter(this.lastNicknameUpdateDate.plusDays(30));
    }
}


