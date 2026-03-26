package com.example.learnmapbackend.service;

import com.example.learnmapbackend.config.CustomWebSocketHandler;
import com.example.learnmapbackend.entity.Notification;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.NotificationRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发送通知（保存到数据库 + 实时推送）
     * @param username 接收通知的用户名
     * @param type 类型（COMMENT / LIKE）
     * @param content 内容
     * @param relatedId 关联的帖子ID
     */
    public void notifyUser(String username, String type, String content, Long relatedId) {
        // 1. 保存到数据库
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在：" + username));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setRead(false);   // 使用 setRead 方法
        notificationRepository.save(notification);

        // 2. 实时推送（如果用户在线）
        CustomWebSocketHandler.sendToUser(username, content);
    }
}