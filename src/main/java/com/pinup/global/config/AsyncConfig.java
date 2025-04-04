package com.pinup.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "imageProcessingExecutor")
    public Executor imageProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);          // 기본 스레드 수
        executor.setMaxPoolSize(5);           // 최대 스레드 수
        executor.setQueueCapacity(100);       // 대기 큐 크기
        executor.setThreadNamePrefix("img-proc-");
        executor.initialize();

        return executor;
    }
}
