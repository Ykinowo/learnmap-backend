package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/post/{postId}")
    public ApiResponse<Boolean> toggleFavorite(HttpServletRequest request, @PathVariable Long postId) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            boolean favorited = favoriteService.toggleFavorite(username, postId);
            return ApiResponse.success(favorited);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/post/{postId}/status")
    public ApiResponse<Boolean> isFavorited(HttpServletRequest request, @PathVariable Long postId) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            boolean favorited = favoriteService.isFavorited(username, postId);
            return ApiResponse.success(favorited);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}