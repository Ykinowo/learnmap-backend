package com.example.learnmapbackend.controller.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long parentId; // 可选
}
