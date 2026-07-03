package com.dasigolmok.domain.reaction.repository;

import com.dasigolmok.domain.reaction.entity.Report;
import com.dasigolmok.domain.reaction.entity.ReportStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, String> {

    @EntityGraph(attributePaths = {"story", "reporter"})
    List<Report> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"story", "reporter"})
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);
}
