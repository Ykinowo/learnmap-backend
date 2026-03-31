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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    public Post createPost(String username, String title, String content,
                           String locationName, Double latitude, Double longitude,
                           String tags, boolean isAnonymous, String imageUrls, String type) {
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
        // 设置审核状态：如果是校方通知直接通过，否则待审核
        if ("official".equals(type)) {
            post.setReviewStatus("approved");
        } else {
            post.setReviewStatus("pending");
        }
        return postRepository.save(post);
    }

    // 获取普通帖子列表（只返回审核通过的）
    public List<Post> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByTypeAndReviewStatusOrderByCreatedAtDesc("normal", "approved", pageable);
    }

    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        // 如果帖子未审核通过且不是校方通知，普通用户无法查看（但管理员可以在管理端查看）
        // 这里普通用户查看时，如果 reviewStatus != 'approved' 且 type != 'official'，则抛出异常
        if (!"approved".equals(post.getReviewStatus()) && !"official".equals(post.getType())) {
            throw new RuntimeException("帖子正在审核中");
        }
        return post;
    }

    public List<WeightedLatLngDTO> getHeatMapData() {
        // 只获取审核通过的普通帖子 + 校方通知（校方通知直接通过）
        List<Post> posts = postRepository.findAll().stream()
                .filter(p -> "approved".equals(p.getReviewStatus()) || "official".equals(p.getType()))
                .collect(Collectors.toList());
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

    public void deletePost(String username, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("无权删除此帖子");
        }
        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
        favoriteRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    public Map<String, Integer> getTagStats() {
        List<Post> allPosts = postRepository.findAll().stream()
                .filter(p -> "approved".equals(p.getReviewStatus()) || "official".equals(p.getType()))
                .collect(Collectors.toList());
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

    public List<LocationStatDTO> getLocationStats(int topN) {
        List<Post> allPosts = postRepository.findAll().stream()
                .filter(p -> "approved".equals(p.getReviewStatus()) || "official".equals(p.getType()))
                .collect(Collectors.toList());
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

    // 管理端：获取指定审核状态的帖子（分页）
    public List<Post> getPostsByReviewStatus(String reviewStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByReviewStatusOrderByCreatedAtDesc(reviewStatus, pageable);
    }

    // 审核帖子
    @Transactional
    public void reviewPost(Long postId, String action) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if ("approve".equals(action)) {
            post.setReviewStatus("approved");
        } else if ("reject".equals(action)) {
            post.setReviewStatus("rejected");
        } else {
            throw new RuntimeException("无效操作");
        }
        postRepository.save(post);
    }

    public List<Post> getMyPosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 按创建时间倒序，返回该用户的所有帖子（不审核状态限制）
        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}