package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    Optional<Like> findByUserIdAndCommentId(Long userId, Long commentId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
    void deleteByPostId(Long postId);
}