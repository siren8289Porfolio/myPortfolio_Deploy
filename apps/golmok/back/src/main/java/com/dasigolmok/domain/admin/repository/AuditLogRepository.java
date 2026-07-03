package com.dasigolmok.domain.admin.repository;

import com.dasigolmok.domain.admin.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
}
