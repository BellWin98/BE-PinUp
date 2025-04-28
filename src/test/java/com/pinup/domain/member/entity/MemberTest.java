package com.pinup.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private static final String EMAIL = "test@example.com";
    private static final String NAME = "TestUser";
    private static final String NICKNAME = "tester";
    private static final String PROFILE_IMAGE_URL = "http://example.com/image.jpg";
    private static final String SOCIAL_ID = "12345";
    private static final String TERMS_OF_MARKETING = "Y";
    private static final LoginType LOGIN_TYPE = LoginType.KAKAO;
    private static final String NEW_NICKNAME = "newTester";
    private static final String NEW_BIO = "새로운 소개글입니다.";
    private static final String NEW_PROFILE_IMAGE_URL = "http://example.com/new-image.jpg";
    private static final String NEW_TERMS_OF_MARKETING = "N";

    @Test
    @DisplayName("Member 생성 테스트")
    void createMember() {
        // when
        Member member = Member.builder()
                .email(EMAIL)
                .name(NAME)
                .nickname(NICKNAME)
                .profileImageUrl(PROFILE_IMAGE_URL)
                .socialId(SOCIAL_ID)
                .termsOfMarketing(TERMS_OF_MARKETING)
                .loginType(LOGIN_TYPE)
                .build();

        // then
        assertThat(member.getEmail()).isEqualTo(EMAIL);
        assertThat(member.getName()).isEqualTo(NAME);
        assertThat(member.getNickname()).isEqualTo(NICKNAME);
        assertThat(member.getProfileImageUrl()).isEqualTo(PROFILE_IMAGE_URL);
        assertThat(member.getProviderId()).isEqualTo(SOCIAL_ID);
        assertThat(member.getTermsOfMarketing()).isEqualTo(TERMS_OF_MARKETING);
        assertThat(member.getLoginType()).isEqualTo(LOGIN_TYPE);
        assertThat(member.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(member.getStatus()).isEqualTo("Y");
    }

    @Test
    @DisplayName("회원 소개글 수정 테스트")
    void updateBio() {
        // given
        Member member = createTestMember();

        // when
        member.updateBio(NEW_BIO);

        // then
        assertThat(member.getBio()).isEqualTo(NEW_BIO);
    }

    @Test
    @DisplayName("회원 닉네임 수정 테스트")
    void updateNickname() {
        // given
        Member member = createTestMember();

        // when
        member.updateNickname(NEW_NICKNAME);

        // then
        assertThat(member.getNickname()).isEqualTo(NEW_NICKNAME);
        assertThat(member.getLastNicknameUpdateDate()).isNotNull();
    }

    @Test
    @DisplayName("프로필 이미지 수정 테스트")
    void updateProfileImageUrl() {
        // given
        Member member = createTestMember();

        // when
        member.updateProfileImageUrl(NEW_PROFILE_IMAGE_URL);

        // then
        assertThat(member.getProfileImageUrl()).isEqualTo(NEW_PROFILE_IMAGE_URL);
    }

    @Test
    @DisplayName("마케팅 약관 동의 상태 변경 테스트")
    void updateTermsOfMarketing() {
        // given
        Member member = createTestMember();

        // when
        member.updateTermsOfMarketing(NEW_TERMS_OF_MARKETING);

        // then
        assertThat(member.getTermsOfMarketing()).isEqualTo(NEW_TERMS_OF_MARKETING);
    }

    @Test
    @DisplayName("닉네임 수정 가능 여부 테스트 - 최초 수정")
    void canUpdateNickname_FirstTime() {
        // given
        Member member = createTestMember();

        // when & then
        assertThat(member.canUpdateNickname()).isTrue();
    }

    @Test
    @DisplayName("닉네임 수정 가능 여부 테스트 - 30일 이전")
    void canUpdateNickname_Before30Days() {
        // given
        Member member = createTestMember();
        member.updateNickname("newNickname");

        // when & then
        assertThat(member.canUpdateNickname()).isFalse();
    }

    @Test
    @DisplayName("닉네임 수정 가능 여부 테스트 - 30일 이후")
    void canUpdateNickname_After30Days() {
        // given
        Member member = createTestMember();
        member.updateNickname("newNickname");
        LocalDateTime thirtyOneDaysAgo = LocalDateTime.now().minusDays(31);
        ReflectionTestUtils.setField(member, "lastNicknameUpdateDate", thirtyOneDaysAgo);

        // when & then
        assertThat(member.canUpdateNickname()).isTrue();
    }

    private Member createTestMember() {
        return Member.builder()
                .email(EMAIL)
                .name(NAME)
                .nickname(NICKNAME)
                .profileImageUrl(PROFILE_IMAGE_URL)
                .socialId(SOCIAL_ID)
                .termsOfMarketing(TERMS_OF_MARKETING)
                .loginType(LOGIN_TYPE)
                .build();
    }
}