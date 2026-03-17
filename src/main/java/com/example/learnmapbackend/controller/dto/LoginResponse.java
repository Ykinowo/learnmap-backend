package com.example.learnmapbackend.controller.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String avatar;
}
