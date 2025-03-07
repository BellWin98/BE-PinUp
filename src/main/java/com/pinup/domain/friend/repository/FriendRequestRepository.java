package com.pinup.domain.friend.repository;

import com.pinup.domain.friend.entity.FriendRequest;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import com.pinup.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findBySenderAndReceiver(Member sender, Member receiver);
    List<FriendRequest> findBySender(Member sender);
    List<FriendRequest> findAllByReceiverAndFriendRequestStatus(Member receiver, FriendRequestStatus status);
    List<FriendRequest> findAllBySenderAndFriendRequestStatus(Member sender, FriendRequestStatus status);
    boolean existsBySenderAndReceiverAndFriendRequestStatus(Member sender, Member receiver, FriendRequestStatus status);
}
