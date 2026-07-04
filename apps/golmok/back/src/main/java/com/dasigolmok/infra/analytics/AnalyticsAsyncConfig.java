package com.dasigolmok.infra.analytics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * REFRESH MATERIALIZED VIEW CONCURRENTLY는 같은 뷰에 대해 동시에 두 번 실행되면
 * 서로 락을 기다리며 대기하게 된다. 좋아요처럼 짧은 시간에 여러 요청이 몰릴 수 있는
 * 쓰기 작업에서 리프레시를 병렬로 여러 개 띄우면 오히려 대기 행렬만 길어지므로,
 * 단일 스레드 큐로 직렬화해 한 번에 하나씩만 리프레시가 실행되도록 한다.
 */
@Configuration
public class AnalyticsAsyncConfig {

    @Bean(name = "analyticsRefreshExecutor")
    public Executor analyticsRefreshExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("analytics-refresh-");
        executor.initialize();
        return executor;
    }
}
