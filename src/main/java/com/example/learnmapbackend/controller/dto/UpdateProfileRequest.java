package com.example.learnmapbackend.controller.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nickname;   // 用户名
    private String bio;         // 个人简介
}