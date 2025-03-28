package com.pinup.domain.friend.entity;


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
public class FriendShip extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendShipStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Member friend;

    @Builder
    public FriendShip(Member member, Member friend) {
        this.member = member;
        this.friend = friend;
        this.status = FriendShipStatus.PENDING;
    }

    public void acceptFriendShip() {
        this.status = FriendShipStatus.ACCEPTED;
    }

    public void rejectFriendShip() {
        this.status = FriendShipStatus.REJECTED;
    }

    public void deleteFriendShip() {
        this.status = FriendShipStatus.DELETED;
    }
}
