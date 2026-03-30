package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByTypeOrderByCreatedAtDesc(String type, Pageable pageable);
}