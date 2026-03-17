package com.example.learnmapbackend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "like_count")
    private int likeCount = 0;

    @Column(name = "parent_id")
    private Long parentId; // 如果是回复某条评论，则记录父评论ID

    @Column(name = "created_at")
    private Date createdAt = new Date();
}