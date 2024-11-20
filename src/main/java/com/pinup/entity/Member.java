package com.pinup.entity;

import com.pinup.global.common.BaseTimeEntity;
import com.pinup.enums.LoginType;
import com.pinup.enums.Role;
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

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<FriendShip> friendships = new ArrayList<>();

    @Builder
    public Member(String email, String name, String nickname,
                  String profileImageUrl, LoginType loginType, String socialId,
                  String password) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
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

    public boolean canUpdateNickname() {
        return this.lastNicknameUpdateDate == null || LocalDateTime.now().isAfter(this.lastNicknameUpdateDate.plusDays(30));
    }
}


