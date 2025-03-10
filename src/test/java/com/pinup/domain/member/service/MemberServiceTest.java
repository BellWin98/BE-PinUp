package com.pinup.domain.member.service;

import com.pinup.domain.friend.repository.FriendRequestRepository;
import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.domain.member.dto.response.MemberInfoResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.SearchMemberResponse;
import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.entity.MemberRelationType;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendShipRepository friendShipRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private S3Service s3Service;

    @Mock
    private ApplicationContext applicationContext;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "TestUser";
    private static final String TEST_NICKNAME = "tester";
    private static final String TEST_PROFILE_IMAGE_URL = "http://example.com/image.jpg";
    private static final String TEST_SOCIAL_ID = "12345";
    private static final String TEST_TERMS_OF_MARKETING = "Y";
    private static final LoginType TEST_LOGIN_TYPE = LoginType.KAKAO;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email(TEST_EMAIL)
                .name(TEST_NAME)
                .nickname(TEST_NICKNAME)
                .profileImageUrl(TEST_PROFILE_IMAGE_URL)
                .socialId(TEST_SOCIAL_ID)
                .termsOfMarketing(TEST_TERMS_OF_MARKETING)
                .loginType(TEST_LOGIN_TYPE)
                .build();
    }

    @Test
    @DisplayName("닉네임으로 회원 검색 성공")
    void searchMembers_Success() {
        // given
        String searchKeyword = "test";
        given(authUtil.getLoginMember()).willReturn(testMember);
        given(memberRepository.findAllByNicknameContaining(searchKeyword)).willReturn(List.of(testMember));

        // when
        List<SearchMemberResponse> result = memberService.searchMembers(searchKeyword);

        // then
        assertThat(result).hasSize(1);
        SearchMemberResponse response = result.get(0);
        assertThat(response.getMemberResponse().getNickname()).isEqualTo(TEST_NICKNAME);
        assertThat(response.getRelationType()).isEqualTo(MemberRelationType.SELF);
    }

    @Test
    @DisplayName("빈 문자열로 검색 시 null 반환")
    void searchMembers_EmptyKeyword_ReturnsNull() {
        // given
        String searchKeyword = "";

        // when
        List<SearchMemberResponse> result = memberService.searchMembers(searchKeyword);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("내 피드 조회 성공")
    void getMyFeed_Success() {
        // given
        given(authUtil.getLoginMember()).willReturn(testMember);
        given(applicationContext.getBean(MemberService.class)).willReturn(memberService);

        // when
        MemberInfoResponse response = memberService.getMyFeed();

        // then
        assertThat(response.getMemberResponse().getNickname()).isEqualTo(TEST_NICKNAME);
        assertThat(response.getReviewCount()).isZero();
        assertThat(response.getPinBuddyCount()).isZero();
    }

    @Test
    @DisplayName("특정 회원 피드 조회 성공")
    void getMemberFeed_Success() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
        given(applicationContext.getBean(MemberService.class)).willReturn(memberService);

        // when
        MemberInfoResponse response = memberService.getMemberFeed(memberId);

        // then
        assertThat(response.getMemberResponse().getNickname()).isEqualTo(TEST_NICKNAME);
        assertThat(response.getReviewCount()).isZero();
        assertThat(response.getPinBuddyCount()).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 회원의 피드 조회 시 예외 발생")
    void getMemberFeed_NotFound_ThrowsException() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberFeed(memberId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("로그인 후 회원 정보 수정 성공")
    void updateInfoAfterLogin_Success() {
        // given
        UpdateMemberInfoAfterLoginRequest request = new UpdateMemberInfoAfterLoginRequest();
        request.setNickname("newNickname");
        request.setTermsOfMarketing("Y");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes());

        given(authUtil.getLoginMember()).willReturn(testMember);
        given(s3Service.uploadFile(anyString(), any())).willReturn("new-image-url");
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when
        MemberResponse response = memberService.updateInfoAfterLogin(request, multipartFile);

        // then
        assertThat(response).isNotNull();
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복된 경우")
    void checkNicknameDuplicate_WhenDuplicate() {
        // given
        String nickname = "existingNickname";
        given(memberRepository.existsByNickname(nickname)).willReturn(true);

        // when
        boolean result = memberService.checkNicknameDuplicate(nickname);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복되지 않은 경우")
    void checkNicknameDuplicate_WhenNotDuplicate() {
        // given
        String nickname = "newNickname";
        given(memberRepository.existsByNickname(nickname)).willReturn(false);

        // when
        boolean result = memberService.checkNicknameDuplicate(nickname);

        // then
        assertThat(result).isFalse();
    }
}