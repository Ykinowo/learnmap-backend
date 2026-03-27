package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    void deleteByPostId(Long postId);
}

