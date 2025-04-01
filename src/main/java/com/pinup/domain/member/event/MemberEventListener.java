package com.pinup.domain.member.event;

import com.pinup.domain.friend.service.FriendShipService;
import com.pinup.domain.member.entity.Member;
import com.pinup.global.common.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberEventListener {

    private final FriendShipService friendShipService;
    private final AuthUtil authUtil;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMemberCreatedEvent(Member member) {
        log.info("유저 생성 이벤트 수신: 유저 ID={}", member.getId());
        Member pinupAccount = authUtil.getValidMember(1L);
        friendShipService.createFriendShip(member, pinupAccount);
    }
}
