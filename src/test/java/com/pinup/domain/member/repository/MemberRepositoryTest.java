package com.pinup.domain.member.repository;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MemberRepositoryTest.TestConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "TestUser";
    private static final String TEST_NICKNAME = "tester";
    private static final String TEST_PROFILE_IMAGE_URL = "http://example.com/image.jpg";
    private static final String TEST_SOCIAL_ID = "12345";
    private static final String TEST_TERMS_OF_MARKETING = "Y";
    private static final LoginType TEST_LOGIN_TYPE = LoginType.KAKAO;

    private Member testMember;

    static class TestConfig {
        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

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
    @DisplayName("Member 저장 테스트")
    void saveMember() {
        // when
        Member savedMember = memberRepository.save(testMember);
        flushAndClear();

        // then
        Member foundMember = memberRepository.findById(savedMember.getId()).orElseThrow();
        assertThat(foundMember.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(foundMember.getName()).isEqualTo(TEST_NAME);
        assertThat(foundMember.getNickname()).isEqualTo(TEST_NICKNAME);
    }

    @Test
    @DisplayName("소셜 ID로 회원 찾기 테스트")
    void findByProviderId() {
        // given
        memberRepository.save(testMember);
        flushAndClear();

        // when
        Optional<Member> foundMember = memberRepository.findByProviderId(TEST_SOCIAL_ID);

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getProviderId()).isEqualTo(TEST_SOCIAL_ID);
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 테스트")
    void existsByNickname() {
        // given
        memberRepository.save(testMember);
        flushAndClear();

        // when
        boolean exists = memberRepository.existsByNickname(TEST_NICKNAME);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임으로 회원 검색 테스트")
    void findAllByNicknameContaining() {
        // given
        memberRepository.save(testMember);

        Member anotherMember = Member.builder()
                .email("another@example.com")
                .name("AnotherUser")
                .nickname("test_another")
                .profileImageUrl("http://example.com/another.jpg")
                .socialId("67890")
                .termsOfMarketing("Y")
                .loginType(TEST_LOGIN_TYPE)
                .build();
        memberRepository.save(anotherMember);
        flushAndClear();

        // when
        List<Member> foundMembers = memberRepository.findAllByNicknameContaining("test");

        // then
        assertThat(foundMembers).hasSize(2);
        assertThat(foundMembers).extracting("nickname")
                .contains(TEST_NICKNAME, "test_another");
    }

    @Test
    @DisplayName("존재하지 않는 소셜 ID로 회원 찾기 테스트")
    void findByProviderId_NotFound() {
        // given
        memberRepository.save(testMember);
        flushAndClear();

        // when
        Optional<Member> foundMember = memberRepository.findByProviderId("nonexistent");

        // then
        assertThat(foundMember).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}