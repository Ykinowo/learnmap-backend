package com.example.learnmapbackend.controller.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String tags;
    private boolean isAnonymous;
    private String imageUrls;  // 新增
}