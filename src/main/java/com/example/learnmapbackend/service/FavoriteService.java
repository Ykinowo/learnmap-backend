package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Favorite;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.FavoriteRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean toggleFavorite(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 封禁用户无法收藏（但封禁用户已无法登录，这个检查可省略，保留以作防御）
        if ("banned".equals(user.getStatus())) {
            throw new RuntimeException("账号已被封禁，无法收藏");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (favoriteRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            favoriteRepository.deleteByUserIdAndPostId(user.getId(), postId);
            return false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setPost(post);
            favoriteRepository.save(favorite);
            return true;
        }
    }

    public boolean isFavorited(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return favoriteRepository.existsByUserIdAndPostId(user.getId(), postId);
    }
}