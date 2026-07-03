package com.dasigolmok.domain.reaction.repository;

import com.dasigolmok.domain.reaction.entity.Reaction;
import com.dasigolmok.domain.reaction.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, String> {
    Optional<Reaction> findByUserIdAndStoryIdAndType(String userId, String storyId, ReactionType type);
    boolean existsByUserIdAndStoryIdAndType(String userId, String storyId, ReactionType type);
}
