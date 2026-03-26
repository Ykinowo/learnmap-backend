package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.entity.Notification;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.NotificationRepository;
import com.example.learnmapbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    // 获取当前用户的所有通知（按时间倒序）
    @GetMapping
    public ApiResponse<List<Notification>> getNotifications(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            User user = userService.getUserByUsername(username);
            List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 标记通知为已读
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
        return ApiResponse.success(null);
    }
}