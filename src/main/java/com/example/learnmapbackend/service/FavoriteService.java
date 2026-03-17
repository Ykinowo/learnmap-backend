package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Favorite;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.FavoriteRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean toggleFavorite(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (favoriteRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            favoriteRepository.deleteByUserIdAndPostId(user.getId(), postId);
            return false; // 取消收藏
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setPost(post);
            favoriteRepository.save(favorite);
            return true; // 收藏成功
        }
    }

    public boolean isFavorited(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return favoriteRepository.existsByUserIdAndPostId(user.getId(), postId);
    }
}