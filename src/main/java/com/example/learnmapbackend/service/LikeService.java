package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Like;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.LikeRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public boolean likePost(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 禁言或封禁用户禁止点赞
        if (!"normal".equals(user.getStatus())) {
            if ("muted".equals(user.getStatus())) {
                throw new RuntimeException("账号已被禁言，无法点赞");
            } else if ("banned".equals(user.getStatus())) {
                throw new RuntimeException("账号已被封禁，无法点赞");
            } else {
                throw new RuntimeException("账号状态异常，请联系管理员");
            }
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return false;
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            User author = post.getUser();
            if (!author.getUsername().equals(username)) {
                notificationService.notifyUser(author.getUsername(), "LIKE", "你的帖子被点赞了", postId);
            }
            return true;
        }
    }

    public boolean isPostLikedByUser(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return likeRepository.existsByUserIdAndPostId(user.getId(), postId);
    }
}