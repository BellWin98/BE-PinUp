package com.pinup.repository;

import com.pinup.domain.friend.repository.FriendRequestRepository;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.domain.friend.entity.FriendRequest;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pinup.constants.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FriendRequestRepositoryTest {

    private Member sender;
    private Member receiver;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() throws Exception {
        friendRequestRepository.deleteAll();
        memberRepository.deleteAll();

        sender = Member.builder()
                .email(TEST_MEMBER_EMAIL)
                .name(TEST_MEMBER_NAME)
                .loginType(TEST_MEMBER_LOGIN_TYPE)
                .profileImageUrl(TEST_MEMBER_IMAGE_URL)
                .nickname(TEST_MEMBER_NICKNAME)
                .socialId(TEST_MEMBER_SOCIAL_ID)
                .build();
        memberRepository.save(sender);

        receiver = Member.builder()
                .email(SECOND_TEST_MEMBER_EMAIL)
                .name(SECOND_TEST_MEMBER_NAME)
                .loginType(TEST_MEMBER_LOGIN_TYPE)
                .profileImageUrl(SECOND_TEST_MEMBER_IMAGE_URL)
                .nickname(SECOND_TEST_MEMBER_NICKNAME)
                .socialId(TEST_MEMBER_SOCIAL_ID)
                .build();
        memberRepository.save(receiver);
    }

    @Test
    @DisplayName("친구 요청을 id로 조회할 수 있다.")
    void 레포지토리에_친구요청이_있으면_id로_조회할_수_있다(){
        //given
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();

        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

        //when
        Optional<FriendRequest> findByIdFriendRequest = friendRequestRepository.findById(savedFriendRequest.getId());

        //then
        assertThat(findByIdFriendRequest).isPresent();
        assertThat(findByIdFriendRequest.get().getId()).isEqualTo(savedFriendRequest.getId());
        assertThat(findByIdFriendRequest.get().getFriendRequestStatus()).isEqualTo(FriendRequestStatus.PENDING);
        assertThat(findByIdFriendRequest.get().getSender()).isEqualTo(sender);
        assertThat(findByIdFriendRequest.get().getReceiver()).isEqualTo(receiver);
    }

    @Test
    @DisplayName("친구 요청을 sender와 receiver로 조회할 수 있다.")
    void findBySenderAndReceiver_테스트() {
        // given
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

        // when
        List<FriendRequest> foundRequests = friendRequestRepository.findBySenderAndReceiver(sender, receiver);

        // then
        assertThat(foundRequests).hasSize(1);
        assertThat(foundRequests.get(0)).isEqualTo(savedFriendRequest);
    }

    @Test
    @DisplayName("sender로 친구 요청을 조회할 수 있다.")
    void findBySender_테스트() {
        // given
        List<FriendRequest> savedRequests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FriendRequest friendRequest = FriendRequest.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            savedRequests.add(friendRequestRepository.save(friendRequest));
        }

        // when
        List<FriendRequest> foundRequests = friendRequestRepository.findBySender(sender);

        // then
        assertThat(foundRequests).hasSize(5);
        assertThat(foundRequests).containsExactlyElementsOf(savedRequests);
    }

    @Test
    @DisplayName("receiver로 친구 요청을 조회할 수 있다.")
    void findByReceiver_테스트() {
        // given
        List<FriendRequest> savedRequests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FriendRequest friendRequest = FriendRequest.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            savedRequests.add(friendRequestRepository.save(friendRequest));
        }

        // when
        List<FriendRequest> foundRequests = friendRequestRepository.findByReceiver(receiver);

        // then
        assertThat(foundRequests).hasSize(5);
        assertThat(foundRequests).containsExactlyElementsOf(savedRequests);
    }
}