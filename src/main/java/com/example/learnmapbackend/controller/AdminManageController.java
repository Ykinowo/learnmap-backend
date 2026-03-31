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
@RequestMapping("/api/admin/manage")
public class AdminManageController {

    @Autowired
    private UserRepository userRepository;

    // 获取管理员列表（角色为 admin 的用户）
    @GetMapping
    public ApiResponse<Map<String, Object>> listAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> adminPage = userRepository.findByRole("admin", pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("admins", adminPage.getContent());
        result.put("totalPages", adminPage.getTotalPages());
        result.put("totalElements", adminPage.getTotalElements());
        result.put("currentPage", page);
        return ApiResponse.success(result);
    }

    // 添加管理员（将普通用户提升为管理员）
    @PostMapping
    public ApiResponse<Void> addAdmin(@RequestParam Long userId, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        user.setRole("admin");
        userRepository.save(user);
        return ApiResponse.success(null);
    }

    // 移除管理员（将管理员降级为普通用户）
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> removeAdmin(@PathVariable Long userId, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        // 不能移除自己
        String currentUsername = (String) session.getAttribute("adminUsername");
        if (user.getUsername().equals(currentUsername)) {
            return ApiResponse.error("不能移除自己的管理员权限");
        }
        user.setRole("user");
        userRepository.save(user);
        return ApiResponse.success(null);
    }

    // 获取普通用户列表（用于添加管理员时选择）
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> listNormalUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByUsernameContaining(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        // 过滤掉已经是管理员的用户
        Map<String, Object> result = new HashMap<>();
        result.put("users", userPage.getContent().stream().filter(u -> !"admin".equals(u.getRole())).toList());
        result.put("totalPages", userPage.getTotalPages());
        result.put("totalElements", userPage.getTotalElements());
        result.put("currentPage", page);
        return ApiResponse.success(result);
    }
}