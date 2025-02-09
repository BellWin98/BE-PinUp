package com.pinup.cache;

import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.ProfileResponse;
import com.pinup.dto.response.ReviewCountsResponse;
import com.pinup.global.config.redis.MemberCacheManager;
import com.pinup.global.config.redis.RedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {
        RedisConfig.class,
        MemberCacheManager.class
})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@TestPropertySource(properties = {
        "spring.redis.host=localhost",
        "spring.redis.port=6379"
})
public class MemberCacheManagerTest {

    @Autowired
    private MemberCacheManager memberCacheManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Long TEST_MEMBER_ID = 1L;
    private static final int CONCURRENT_REQUESTS = 10;
    private static final int THREAD_POOL_AWAIT_TIMEOUT = 10;

    @BeforeEach
    void setUp() {
        memberCacheManager.evictAllCaches(TEST_MEMBER_ID);
    }

    private MemberResponse createTestMemberResponse() {
        return MemberResponse.builder()
                .memberId(TEST_MEMBER_ID)
                .email("test@gmail.com")
                .name("테스트")
                .nickname("test-user")
                .profilePictureUrl("https://example.com/profile.jpg")
                .bio("테스트 사용자입니다")
                .build();
    }

    private ProfileResponse createTestProfileResponse() {
        return ProfileResponse.builder()
                .member(createTestMemberResponse())
                .reviewCount(5)
                .reviewCounts(new ReviewCountsResponse())
                .friendCount(10)
                .reviews(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("동시에 여러 요청이 들어와도 멤버 캐시 적재는 한번만 실행된다")
    void 동시_요청_시_멤버_캐시_적재는_한번만_실행된다() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        try {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(CONCURRENT_REQUESTS);
            AtomicInteger loaderCallCount = new AtomicInteger(0);

            for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
                executorService.submit(() -> {
                    try {
                        if (!startLatch.await(5, TimeUnit.SECONDS)) {
                            fail("시작 대기 시간 초과");
                        }

                        MemberResponse response = memberCacheManager.getMemberWithCache(
                                TEST_MEMBER_ID,
                                () -> {
                                    loaderCallCount.incrementAndGet();
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        throw new RuntimeException("로더 실행 중 인터럽트 발생", e);
                                    }
                                    return createTestMemberResponse();
                                }
                        );

                        assertThat(response.getMemberId()).isEqualTo(TEST_MEMBER_ID);
                        assertThat(response.getEmail()).isEqualTo("test@gmail.com");
                        assertThat(response.getName()).isEqualTo("테스트");
                        assertThat(response.getNickname()).isEqualTo("test-user");
                        assertThat(response.getProfilePictureUrl()).isEqualTo("https://example.com/profile.jpg");
                        assertThat(response.getBio()).isEqualTo("테스트 사용자입니다");
                    } catch (Exception e) {
                        fail("테스트 실행 중 예외 발생: " + e.getMessage());
                    } finally {
                        endLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            if (!endLatch.await(10, TimeUnit.SECONDS)) {
                fail("테스트 완료 대기 시간 초과");
            }

            assertThat(loaderCallCount.get()).isEqualTo(1);
            assertThat(memberCacheManager.getMemberCache(TEST_MEMBER_ID)).isPresent();

        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(THREAD_POOL_AWAIT_TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }
    }

    @Test
    @DisplayName("동시에 여러 요청이 들어와도 프로필 캐시 적재는 한번만 실행된다")
    void 동시_요청_시_프로필_캐시_적재는_한번만_실행된다() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        try {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(CONCURRENT_REQUESTS);
            AtomicInteger loaderCallCount = new AtomicInteger(0);

            for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
                executorService.submit(() -> {
                    try {
                        if (!startLatch.await(5, TimeUnit.SECONDS)) {
                            fail("시작 대기 시간 초과");
                        }

                        ProfileResponse response = memberCacheManager.getProfileWithCache(
                                TEST_MEMBER_ID,
                                () -> {
                                    loaderCallCount.incrementAndGet();
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        throw new RuntimeException("로더 실행 중 인터럽트 발생", e);
                                    }
                                    return createTestProfileResponse();
                                }
                        );

                        assertThat(response.getMember()).usingRecursiveComparison()
                                .isEqualTo(createTestMemberResponse());
                        assertThat(response.getReviewCount()).isEqualTo(5);
                        assertThat(response.getFriendCount()).isEqualTo(10);

                    } catch (Exception e) {
                        fail("테스트 실행 중 예외 발생: " + e.getMessage());
                    } finally {
                        endLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            if (!endLatch.await(10, TimeUnit.SECONDS)) {
                fail("테스트 완료 대기 시간 초과");
            }

            assertThat(loaderCallCount.get()).isEqualTo(1);
            assertThat(memberCacheManager.getProfileCache(TEST_MEMBER_ID)).isPresent();

        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(THREAD_POOL_AWAIT_TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }
    }

    @Test
    @DisplayName("캐시 만료시간이 임계치에 도달하면 자동으로 갱신된다")
    void 캐시_만료시간_임계치_도달시_자동_갱신() throws Exception {
        MemberResponse initialResponse = memberCacheManager.getMemberWithCache(
                TEST_MEMBER_ID,
                this::createTestMemberResponse
        );

        String key = "member:" + TEST_MEMBER_ID;
        redisTemplate.expire(key, java.time.Duration.ofMillis(1));

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("대기 중 인터럽트 발생", e);
        }

        MemberResponse refreshedResponse = memberCacheManager.getMemberWithCache(
                TEST_MEMBER_ID,
                this::createTestMemberResponse
        );

        Long ttl = redisTemplate.getExpire(key);
        assertThat(ttl).isGreaterThan(0);
        assertThat(refreshedResponse)
                .usingRecursiveComparison()
                .isEqualTo(initialResponse);
    }
}