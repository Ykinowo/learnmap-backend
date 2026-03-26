package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.BrowseHistory;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.repository.BrowseHistoryRepository;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BrowseHistoryService {

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public void recordBrowse(String username, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        // 如果已有记录，更新时间；否则新增
        BrowseHistory existing = browseHistoryRepository.findByUserIdAndPostId(user.getId(), postId)
                .orElse(null);
        if (existing != null) {
            existing.setBrowseTime(new Date());
            browseHistoryRepository.save(existing);
        } else {
            BrowseHistory history = new BrowseHistory();
            history.setUser(user);
            history.setPost(post);
            browseHistoryRepository.save(history);
        }
    }

    public List<BrowseHistory> getUserBrowseHistory(Long userId) {
        return browseHistoryRepository.findByUserIdOrderByBrowseTimeDesc(userId);
    }
}