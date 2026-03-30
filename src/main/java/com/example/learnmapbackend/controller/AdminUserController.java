package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户列表（分页 + 搜索）
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        // 简单验证管理员登录
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (keyword != null && !keyword.isBlank()) {
            // 按用户名或ID搜索（这里简单按用户名模糊查询）
            userPage = userRepository.findByUsernameContaining(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("users", userPage.getContent());
        result.put("totalPages", userPage.getTotalPages());
        result.put("totalElements", userPage.getTotalElements());
        result.put("currentPage", page);

        return ApiResponse.success(result);
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status, // normal, muted, banned
            HttpSession session) {
        System.out.println("Session adminToken: " + session.getAttribute("adminToken"));
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        user.setStatus(status);
        userRepository.save(user);
        return ApiResponse.success(null);
    }
}