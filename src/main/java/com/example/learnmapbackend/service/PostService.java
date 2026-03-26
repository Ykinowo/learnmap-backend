package com.example.learnmapbackend.service;

import com.example.learnmapbackend.controller.dto.WeightedLatLngDTO;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(String username, String title, String content,
                           String locationName, Double latitude, Double longitude,
                           String tags, boolean isAnonymous, String imageUrls) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
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
        return postRepository.save(post);
    }

    public List<Post> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        // 对每个帖子处理匿名
        for (Post post : posts) {
            if (post.isAnonymous()) {
                anonymizePostUser(post);
            }
        }
        return posts;
    }

    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (post.isAnonymous()) {
            anonymizePostUser(post);
        }
        return post;
    }

    private void anonymizePostUser(Post post) {
        User anonymous = new User();
        anonymous.setId(0L);
        anonymous.setUsername("匿名用户");
        anonymous.setEmail(null);
        anonymous.setAvatar(null);
        anonymous.setBio(null);
        post.setUser(anonymous);
        // 可选：打印日志确认
        System.out.println("Anonymized post " + post.getId() + " user to 匿名用户");
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
}