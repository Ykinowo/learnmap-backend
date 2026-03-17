package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(String username, String title, String content,
                           String locationName, Double latitude, Double longitude,
                           String tags, boolean isAnonymous) {
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
}