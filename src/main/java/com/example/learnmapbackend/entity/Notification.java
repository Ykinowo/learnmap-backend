package com.example.learnmapbackend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;          // 接收通知的用户

    private String type;        // 类型：COMMENT, LIKE

    private String content;     // 消息内容

    private Long relatedId;     // 关联的帖子ID

    private boolean read;       // 是否已读

    @Column(name = "created_at")
    private Date createdAt = new Date();
}