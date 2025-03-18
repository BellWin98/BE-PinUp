package com.pinup.domain.friend.service;

import com.pinup.domain.alarm.entity.Alarm;
import com.pinup.domain.alarm.repository.AlarmRepository;
import com.pinup.domain.alarm.service.NotificationService;
import com.pinup.domain.friend.dto.response.FriendRequestResponse;
import com.pinup.domain.friend.entity.FriendRequest;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import com.pinup.domain.friend.exception.FriendRequestReceiverMismatchException;
import com.pinup.domain.friend.exception.FriendRequestSenderMismatchException;
import com.pinup.domain.friend.exception.SelfFriendRequestException;
import com.pinup.domain.friend.repository.FriendRequestRepository;
import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pinup.domain.friend.entity.FriendRequestStatus.PENDING;

@RequiredArgsConstructor
@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendShipRepository friendShipRepository;
    private final MemberRepository memberRepository;
    private final FriendShipService friendShipService;
    private final NotificationService notificationService;
    private final AlarmRepository alarmRepository;
    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    public Page<FriendRequestResponse> getReceivedFriendRequests(Pageable pageable) {
        Member loginMember = authUtil.getLoginMember();

        return friendRequestRepository.findAllByReceiverAndFriendRequestStatus(loginMember, PENDING, pageable)
                .map(FriendRequestResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<FriendRequestResponse> getSentFriendRequests(Pageable pageable) {
        Member loginMember = authUtil.getLoginMember();

        return friendRequestRepository.findAllBySenderAndFriendRequestStatus(loginMember, PENDING, pageable)
                .map(FriendRequestResponse::from);
    }

    @Transactional
    public FriendRequestResponse sendFriendRequest(Long receiverId) {
        Member sender = authUtil.getLoginMember();
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        validateSelfFriendRequest(sender.getSocialId(), receiver.getSocialId());
        validateDuplicateFriendRequest(sender, receiver);
        validateAlreadyFriend(sender, receiver);
        FriendRequest savedFriendRequest = friendRequestRepository.save(new FriendRequest(sender, receiver));
        String message = sender.getName() + "님이 친구 요청을 보냈습니다.";
        // notificationService.sendNotification(receiver.getEmail(), message);
        alarmRepository.save(new Alarm(receiver, message));

        return FriendRequestResponse.from(savedFriendRequest);
    }

    @Transactional
    public void cancelFriendRequest(final Long friendRequestId) {
        Member loginMember = authUtil.getLoginMember();
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        validateRequestSenderIsCurrentUser(loginMember.getSocialId(), friendRequest.getSender().getSocialId());
        friendRequestRepository.delete(friendRequest);
    }

    @Transactional
    public FriendRequestResponse acceptFriendRequest(Long friendRequestId) {
        Member loginMember = authUtil.getLoginMember();
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        validateRequestReceiverIsCurrentUser(loginMember.getSocialId(), friendRequest.getReceiver().getSocialId());
        validateFriendRequestStatus(friendRequest.getFriendRequestStatus());
        friendRequest.accept();
        friendRequestRepository.save(friendRequest);
        friendShipService.createFriendShip(friendRequest.getSender(), friendRequest.getReceiver());
        String message = friendRequest.getReceiver().getName() + "님이 친구 요청을 수락했습니다.";
//        notificationService.sendNotification(friendRequest.getSender().getEmail(), message);
        alarmRepository.save(new Alarm(friendRequest.getSender(), message));

        return FriendRequestResponse.from(friendRequest);
    }

    @Transactional
    public FriendRequestResponse rejectFriendRequest(Long friendRequestId) {
        Member loginMember = authUtil.getLoginMember();
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        validateRequestReceiverIsCurrentUser(loginMember.getSocialId(), friendRequest.getReceiver().getSocialId());
        validateFriendRequestStatus(friendRequest.getFriendRequestStatus());
        friendRequest.reject();
        friendRequestRepository.save(friendRequest);

        String message = friendRequest.getReceiver().getName() + "님이 친구 요청을 거절했습니다.";
        // notificationService.sendNotification(friendRequest.getSender().getEmail(), message);
        alarmRepository.save(new Alarm(friendRequest.getSender(), message));

        return FriendRequestResponse.from(friendRequest);
    }

    private void validateSelfFriendRequest(String senderSocialId, String receiverSocialId) {
        if (senderSocialId.equals(receiverSocialId)) {
            throw new SelfFriendRequestException();
        }
    }

    private void validateDuplicateFriendRequest(Member sender, Member receiver) {
        if (friendRequestRepository.existsBySenderAndReceiverAndFriendRequestStatus(sender, receiver, PENDING)) {
            throw new EntityAlreadyExistException(ErrorCode.ALREADY_EXIST_FRIEND_REQUEST);
        }
    }

    private void validateAlreadyFriend(Member sender, Member receiver) {
        if (friendShipRepository.existsByMemberAndFriend(sender, receiver)) {
            throw new EntityAlreadyExistException(ErrorCode.ALREADY_FRIEND);
        }
    }

    private void validateFriendRequestStatus(FriendRequestStatus status) {
        if (status != PENDING) {
            throw new EntityAlreadyExistException(ErrorCode.ALREADY_PROCESSED_FRIEND_REQUEST);
        }
    }

    private void validateRequestReceiverIsCurrentUser(String currentMemberSocialId, String receiverSocialId) {
        if (!currentMemberSocialId.equals(receiverSocialId)) {
            throw new FriendRequestReceiverMismatchException();
        }
    }

    private void validateRequestSenderIsCurrentUser(String currentMemberSocialId, String senderSocialId) {
        if (!currentMemberSocialId.equals(senderSocialId)) {
            throw new FriendRequestSenderMismatchException();
        }
    }
}
