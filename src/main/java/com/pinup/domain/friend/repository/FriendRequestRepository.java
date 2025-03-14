package com.pinup.domain.friend.repository;

import com.pinup.domain.friend.entity.FriendRequest;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import com.pinup.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndReceiverAndFriendRequestStatus(Member loginMember, Member targetMember, FriendRequestStatus status);
    List<FriendRequest> findAllByReceiverAndFriendRequestStatus(Member receiver, FriendRequestStatus status);
    List<FriendRequest> findAllBySenderAndFriendRequestStatus(Member sender, FriendRequestStatus status);
    boolean existsBySenderAndReceiverAndFriendRequestStatus(Member sender, Member receiver, FriendRequestStatus status);
}
