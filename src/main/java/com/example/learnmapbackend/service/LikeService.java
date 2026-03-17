package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Like;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.LikeRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean likePost(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return false; // 取消点赞
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return true; // 点赞成功
        }
    }

    public boolean isPostLikedByUser(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return likeRepository.existsByUserIdAndPostId(user.getId(), postId);
    }
}