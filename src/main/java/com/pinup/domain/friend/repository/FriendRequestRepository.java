package com.pinup.domain.friend.repository;

import com.pinup.domain.friend.entity.FriendRequest;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findBySenderAndReceiver(Member sender, Member receiver);

    List<FriendRequest> findBySender(Member sender);

    List<FriendRequest> findByReceiver(Member receiver);

    Optional<FriendRequest> findBySenderAndReceiverAndFriendRequestStatus(Member sender, Member receiver, FriendRequestStatus status);
}
