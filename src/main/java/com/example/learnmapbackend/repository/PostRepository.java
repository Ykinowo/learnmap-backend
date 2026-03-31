package com.example.learnmapbackend.repository;

import com.example.learnmapbackend.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 普通帖子按创建时间倒序（用于前端，需过滤审核通过的）
    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 校方通知按类型和时间倒序
    List<Post> findByTypeOrderByCreatedAtDesc(String type, Pageable pageable);

    // 新增：按审核状态查询（不分类型，用于管理端）
    List<Post> findByReviewStatusOrderByCreatedAtDesc(String reviewStatus, Pageable pageable);

    // 按审核状态和类型查询（可选，用于筛选普通帖子的审核）
    List<Post> findByTypeAndReviewStatusOrderByCreatedAtDesc(String type, String reviewStatus, Pageable pageable);

    // 添加方法
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}