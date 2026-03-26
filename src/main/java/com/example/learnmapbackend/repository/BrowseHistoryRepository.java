package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.BrowseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {
    List<BrowseHistory> findByUserIdOrderByBrowseTimeDesc(Long userId);
    Optional<BrowseHistory> findByUserIdAndPostId(Long userId, Long postId);
}