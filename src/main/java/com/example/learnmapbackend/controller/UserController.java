package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.controller.dto.UpdateProfileRequest;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 更新当前登录用户的个人资料
     */
    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(HttpServletRequest request,
                                           @RequestBody UpdateProfileRequest updateRequest) {
        // 从请求属性中获取用户名（由 JwtRequestFilter 设置）
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            User updatedUser = userService.updateProfile(username,
                    updateRequest.getNickname(), updateRequest.getBio());
            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}