package com.dasigolmok.infra.analytics;

/**
 * 스토리 등록/좋아요/관리자 승인 등 쓰기 트랜잭션이 커밋된 뒤,
 * Materialized View 리프레시가 필요함을 알리기 위해 발행되는 이벤트.
 */
public class AnalyticsRefreshRequestedEvent {
}
