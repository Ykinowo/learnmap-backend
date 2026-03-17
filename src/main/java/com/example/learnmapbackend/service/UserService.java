package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.UserRepository;
import com.example.learnmapbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public User register(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 实际应加密，这里简化
        user.setEmail(email);
        user.setBio("这个人很懒，什么都没写");
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtil.generateToken(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}