package com.dasigolmok.domain.story.repository;

import com.dasigolmok.domain.story.entity.Story;
import com.dasigolmok.domain.story.entity.StoryStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, String> {

    @EntityGraph(attributePaths = {"user", "region", "place", "images"})
    @Query("""
            SELECT s FROM Story s
            WHERE s.deletedAt IS NULL AND s.status = 'APPROVED'
            AND (:regionId IS NULL OR s.region.id = :regionId)
            """)
    List<Story> findApprovedForMap(@Param("regionId") String regionId);

    @EntityGraph(attributePaths = {"user", "region", "place", "images", "tags"})
    @Query("""
            SELECT s FROM Story s
            WHERE s.deletedAt IS NULL AND s.status = :status
            ORDER BY s.createdAt DESC
            """)
    List<Story> findForAdmin(@Param("status") StoryStatus status, org.springframework.data.domain.Pageable pageable);

    @EntityGraph(attributePaths = {"user", "region", "place", "images", "tags"})
    Optional<Story> findByIdAndDeletedAtIsNull(String id);
}
