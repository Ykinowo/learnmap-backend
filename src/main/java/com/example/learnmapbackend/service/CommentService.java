package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Comment;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.CommentRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public Comment addComment(String username, Long postId, String content, Long parentId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 禁言或封禁用户禁止评论
        if (!"normal".equals(user.getStatus())) {
            if ("muted".equals(user.getStatus())) {
                throw new RuntimeException("账号已被禁言，无法评论");
            } else if ("banned".equals(user.getStatus())) {
                throw new RuntimeException("账号已被封禁，无法评论");
            } else {
                throw new RuntimeException("账号状态异常，请联系管理员");
            }
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        comment.setParentId(parentId);
        Comment saved = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        User author = post.getUser();
        if (!author.getUsername().equals(username)) {
            notificationService.notifyUser(author.getUsername(), "COMMENT", "你的帖子被评论了：" + content, postId);
        }

        return saved;
    }

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}