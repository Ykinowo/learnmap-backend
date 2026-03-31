package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Page<User> findByRole(String role, Pageable pageable);  // 查询管理员
}
