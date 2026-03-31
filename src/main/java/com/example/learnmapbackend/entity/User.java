package com.example.learnmapbackend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;  // 存储加密后的密码

    private String email;

    private String avatar;

    private String bio;       // 个人简介

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "status")
    private String status = "normal"; // normal: 正常, muted: 禁言, banned: 封禁

    private String role = "user";   // user 或 admin
}