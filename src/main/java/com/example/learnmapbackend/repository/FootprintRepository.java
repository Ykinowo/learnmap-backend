package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.Footprint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FootprintRepository extends JpaRepository<Footprint, Long> {
    List<Footprint> findByUserIdOrderByVisitTimeDesc(Long userId);
}