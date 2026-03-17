package com.example.learnmapbackend.controller.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String tags;      // 逗号分隔
    private boolean isAnonymous;
}
