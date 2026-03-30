package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.repository.CommentRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/realtime")
    public ApiResponse<Map<String, Object>> getRealtimeStats() {
        Map<String, Object> stats = new HashMap<>();

        long todayPosts = postRepository.findAll().stream()
                .filter(p -> isToday(p.getCreatedAt()))
                .count();
        long todayComments = commentRepository.findAll().stream()
                .filter(c -> isToday(c.getCreatedAt()))
                .count();
        long totalUsers = userRepository.count();
        long onlineUsers = (long) (totalUsers * 0.3); // 模拟在线数，后续可改进
        long activeUsers = postRepository.findAll().stream()
                .filter(p -> isToday(p.getCreatedAt()))
                .map(p -> p.getUser().getId())
                .distinct()
                .count();
        double activeRatio = totalUsers == 0 ? 0 : (double) activeUsers / totalUsers * 100;

        stats.put("onlineUsers", onlineUsers);
        stats.put("todayPosts", todayPosts);
        stats.put("todayComments", todayComments);
        stats.put("activeRatio", String.format("%.1f", activeRatio) + "%");

        return ApiResponse.success(stats);
    }

    @GetMapping("/post-trend")
    public ApiResponse<Map<String, List<Object>>> getPostTrend() {
        List<LocalDate> last7Days = getLast7Days();
        List<Object> postCounts = new ArrayList<>();
        List<Object> commentCounts = new ArrayList<>();

        for (LocalDate date : last7Days) {
            long posts = postRepository.findAll().stream()
                    .filter(p -> isSameDay(p.getCreatedAt(), date))
                    .count();
            long comments = commentRepository.findAll().stream()
                    .filter(c -> isSameDay(c.getCreatedAt(), date))
                    .count();
            postCounts.add(posts);
            commentCounts.add(comments);
        }

        Map<String, List<Object>> result = new HashMap<>();
        result.put("dates", last7Days.stream().map(LocalDate::toString).collect(Collectors.toList()));
        result.put("posts", postCounts);
        result.put("comments", commentCounts);
        return ApiResponse.success(result);
    }

    @GetMapping("/user-trend")
    public ApiResponse<Map<String, List<Object>>> getUserTrend() {
        List<LocalDate> last7Days = getLast7Days();
        List<Object> userCounts = new ArrayList<>();

        for (LocalDate date : last7Days) {
            long users = postRepository.findAll().stream()
                    .filter(p -> isSameDay(p.getCreatedAt(), date))
                    .map(p -> p.getUser().getId())
                    .distinct()
                    .count();
            userCounts.add(users);
        }

        Map<String, List<Object>> result = new HashMap<>();
        result.put("dates", last7Days.stream().map(LocalDate::toString).collect(Collectors.toList()));
        result.put("users", userCounts);
        return ApiResponse.success(result);
    }

    private boolean isToday(Date date) {
        return isSameDay(date, LocalDate.now());
    }

    private boolean isSameDay(Date date, LocalDate localDate) {
        if (date == null) return false;
        LocalDate d = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return d.equals(localDate);
    }

    private List<LocalDate> getLast7Days() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i));
        }
        return dates;
    }
}