package com.pinup.cache;

import com.pinup.domain.member.dto.response.MemberResponse;
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

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
public class MemberCacheManagerPerformanceTest {

    @Autowired
    private MemberCacheManager memberCacheManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int VIRTUAL_USERS = 50;
    private static final int TEST_DURATION_SECONDS = 60;
    private static final int EXPECTED_RPS = 50;
    private static final Random random = new Random();

    private List<Long> memberIds;
    private Map<String, List<Long>> responseTimes;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        memberIds = IntStream.range(0, 100)
                .mapToObj(i -> (long) i)
                .collect(Collectors.toList());

        responseTimes = new ConcurrentHashMap<>();
        responseTimes.put("before", new ArrayList<>());
        responseTimes.put("after", new ArrayList<>());

        executorService = Executors.newFixedThreadPool(VIRTUAL_USERS);
    }

    private void simulateSlowDatabase() {
        try {
            Thread.sleep(300 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("캐시 도입 전후 성능 비교")
    void comparePerformance() throws Exception {
        System.out.println("캐시 도입 전 성능 테스트 시작");
        runPerformanceTest(false);

        warmupCache();

        System.out.println("캐시 도입 후 성능 테스트 시작");
        runPerformanceTest(true);

        printResults();
    }

    private void warmupCache() {
        memberIds.forEach(memberId ->
                memberCacheManager.getMemberWithCache(memberId, () -> createTestMemberResponse(memberId))
        );
    }

    private void runPerformanceTest(boolean useCache) throws Exception {
        Instant startTime = Instant.now();
        CountDownLatch completionLatch = new CountDownLatch(VIRTUAL_USERS);
        String testType = useCache ? "after" : "before";

        for (int i = 0; i < VIRTUAL_USERS; i++) {
            executorService.submit(() -> {
                try {
                    while (Duration.between(startTime, Instant.now()).getSeconds() < TEST_DURATION_SECONDS) {
                        Long memberId = memberIds.get(random.nextInt(memberIds.size()));
                        Instant requestStart = Instant.now();

                        if (useCache) {
                            memberCacheManager.getMemberWithCache(memberId, () -> {
                                simulateSlowDatabase();
                                return createTestMemberResponse(memberId);
                            });
                        } else {
                            simulateSlowDatabase();
                            createTestMemberResponse(memberId);
                        }

                        long responseTime = Duration.between(requestStart, Instant.now()).toMillis();
                        responseTimes.get(testType).add(responseTime);

                        Thread.sleep(1000 / EXPECTED_RPS);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        completionLatch.await();
    }

    private void printResults() {
        System.out.println("\n성능 테스트 결과");
        System.out.println("=================");

        for (String type : Arrays.asList("before", "after")) {
            List<Long> times = responseTimes.get(type);

            System.out.printf("\n%s 캐시 도입:\n", type.equals("before") ? "전" : "후");
            System.out.printf("총 요청 수: %d\n", times.size());
            System.out.printf("평균 응답 시간: %.2fms\n", times.stream().mapToDouble(Long::doubleValue).average().orElse(0));
            System.out.printf("실제 RPS: %.2f\n", times.size() / (double) TEST_DURATION_SECONDS);
        }
    }

    private MemberResponse createTestMemberResponse(Long memberId) {
        return MemberResponse.builder()
                .memberId(memberId)
                .email("test" + memberId + "@gmail.com")
                .name("테스트" + memberId)
                .nickname("test-user-" + memberId)
                .profilePictureUrl("https://example.com/profile" + memberId + ".jpg")
                .bio("테스트 사용자 " + memberId + "입니다")
                .build();
    }
}