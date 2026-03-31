package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    Page<Tag> findAllByOrderByUseCountDesc(Pageable pageable);
    Page<Tag> findByNameContaining(String keyword, Pageable pageable);
}