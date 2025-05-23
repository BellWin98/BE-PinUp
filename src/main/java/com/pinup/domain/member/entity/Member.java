package com.pinup.domain.member.entity;

import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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

    private String email;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "VARCHAR(12)", unique = true)
    @Audited
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "VARCHAR(1)")
    private String status;

    @Column(columnDefinition = "VARCHAR(150)")
    private String bio;

    private LocalDateTime lastNicknameUpdateDate;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(unique = true)
    private String providerId;

    private String profileImageUrl;

    @Column(columnDefinition = "VARCHAR(1)")
    private String termsOfMarketing = "Y";

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FriendShip> friendships = new ArrayList<>();

    @Builder
    public Member(
            String email, String name, String nickname,
            String providerId, String termsOfMarketing, LoginType loginType
    ) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.providerId = providerId;
        this.termsOfMarketing = termsOfMarketing;
        this.loginType = loginType;
        this.role = Role.ROLE_USER;
        this.status = "Y";
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.lastNicknameUpdateDate = LocalDateTime.now();
    }

    public void updateTermsOfMarketing(String termsOfMarketing) {
        this.termsOfMarketing = termsOfMarketing;
    }

    public boolean canUpdateNickname() {
        return this.lastNicknameUpdateDate == null || LocalDateTime.now().isAfter(this.lastNicknameUpdateDate.plusDays(30));
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void removeProfileImage() {
        this.profileImageUrl = "";
    }
}


