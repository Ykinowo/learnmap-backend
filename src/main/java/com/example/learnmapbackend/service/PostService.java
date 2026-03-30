package com.example.learnmapbackend.service;

import com.example.learnmapbackend.controller.dto.LocationStatDTO;
import com.example.learnmapbackend.controller.dto.WeightedLatLngDTO;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(String username, String title, String content,
                           String locationName, Double latitude, Double longitude,
                           String tags, boolean isAnonymous, String imageUrls,String type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!"normal".equals(user.getStatus())) {
            if ("muted".equals(user.getStatus())) {
                throw new RuntimeException("账号已被禁言，无法发帖");
            } else if ("banned".equals(user.getStatus())) {
                throw new RuntimeException("账号已被封禁，无法发帖");
            } else {
                throw new RuntimeException("账号状态异常，请联系管理员");
            }
        }

        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setContent(content);
        post.setLocationName(locationName);
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setTags(tags);
        post.setAnonymous(isAnonymous);
        post.setImageUrls(imageUrls);
        post.setType(type != null ? type : "normal");
        return postRepository.save(post);
    }

    public List<Post> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
    }

    public List<WeightedLatLngDTO> getHeatMapData() {
        List<Post> posts = postRepository.findAll();
        Map<String, Integer> locationCount = new HashMap<>();
        for (Post post : posts) {
            String key = String.format("%.4f,%.4f", post.getLatitude(), post.getLongitude());
            locationCount.put(key, locationCount.getOrDefault(key, 0) + 1);
        }
        List<WeightedLatLngDTO> heatData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : locationCount.entrySet()) {
            String[] latLng = entry.getKey().split(",");
            double lat = Double.parseDouble(latLng[0]);
            double lng = Double.parseDouble(latLng[1]);
            WeightedLatLngDTO dto = new WeightedLatLngDTO();
            dto.setLat(lat);
            dto.setLng(lng);
            dto.setWeight(entry.getValue());
            heatData.add(dto);
        }
        return heatData;
    }

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    public void deletePost(String username, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("无权删除此帖子");
        }
        // 1. 删除评论
        commentRepository.deleteByPostId(postId);
        // 2. 删除点赞
        likeRepository.deleteByPostId(postId);
        // 3. 删除收藏
        favoriteRepository.deleteByPostId(postId);
        // 4. 最后删除帖子
        postRepository.delete(post);

    }

    // 在 PostService 中添加以下方法

    /**
     * 统计各标签的帖子数量（一个帖子有多个标签则每个标签都计数）
     */
    public Map<String, Integer> getTagStats() {
        List<Post> allPosts = postRepository.findAll();
        Map<String, Integer> tagCount = new HashMap<>();
        for (Post post : allPosts) {
            if (post.getTags() != null && !post.getTags().isBlank()) {
                String[] tags = post.getTags().split(",");
                for (String tag : tags) {
                    String trimmed = tag.trim();
                    if (!trimmed.isEmpty()) {
                        tagCount.put(trimmed, tagCount.getOrDefault(trimmed, 0) + 1);
                    }
                }
            }
        }
        return tagCount;
    }

    /**
     * 统计各地点的帖子数量，返回前 topN 个地点
     */
    public List<LocationStatDTO> getLocationStats(int topN) {
        List<Post> allPosts = postRepository.findAll();
        Map<String, Integer> locationCount = new HashMap<>();
        for (Post post : allPosts) {
            String loc = post.getLocationName();
            if (loc == null || loc.isBlank()) {
                loc = "未知地点";
            }
            locationCount.put(loc, locationCount.getOrDefault(loc, 0) + 1);
        }
        return locationCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    LocationStatDTO dto = new LocationStatDTO();
                    dto.setLocation(entry.getKey());
                    dto.setCount(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}