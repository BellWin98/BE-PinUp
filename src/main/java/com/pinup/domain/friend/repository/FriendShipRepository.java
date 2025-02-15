package com.pinup.domain.friend.repository;

import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    List<FriendShip> findAllByMember(Member member);
    boolean existsByMemberAndFriend(Member member, Member friend);
    Optional<FriendShip> findByMemberAndFriend(Member member, Member friend);
}
