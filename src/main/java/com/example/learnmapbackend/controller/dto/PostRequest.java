package com.example.learnmapbackend.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String tags;
    @JsonProperty("isAnonymous")
    private boolean isAnonymous;
    private String imageUrls;
    private String type = "normal";  // 默认帖子类型普通
}