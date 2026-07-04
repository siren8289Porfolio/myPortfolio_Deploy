package com.allochub.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
// findTop100ByOrderByCreatedAtDesc()가 created_at 기준 정렬 후 상위 100건만 잘라내는데,
// 인덱스가 없으면 로그가 쌓일수록 전체 테이블을 읽어 정렬(Full Scan + Sort)하게 된다.
@Table(
        name = "audit_logs",
        indexes = @Index(name = "idx_audit_logs_created_at", columnList = "created_at DESC"))
public class AuditLog {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
