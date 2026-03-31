package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.controller.dto.PostRequest;
import com.example.learnmapbackend.controller.dto.WeightedLatLngDTO;
import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.service.BrowseHistoryService;
import com.example.learnmapbackend.service.LikeService;
import com.example.learnmapbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;  // 注意：添加注入

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @PostMapping
    public ApiResponse<Post> createPost(HttpServletRequest request, @RequestBody PostRequest postRequest) {
        System.out.println("Received postRequest: " + postRequest);
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            String type = postRequest.getType();
            Post post = postService.createPost(
                    username,
                    postRequest.getTitle(),
                    postRequest.getContent(),
                    postRequest.getLocationName(),
                    postRequest.getLatitude(),
                    postRequest.getLongitude(),
                    postRequest.getTags(),
                    postRequest.isAnonymous(),
                    postRequest.getImageUrls(),
                    type
            );
            return ApiResponse.success(post);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<Post>> getPosts(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Post> posts = postService.getPosts(page, size);
            return ApiResponse.success(posts);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{postId}/like")
    public ApiResponse<Boolean> likePost(HttpServletRequest request, @PathVariable Long postId) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            boolean liked = likeService.likePost(username, postId);
            return ApiResponse.success(liked);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{postId}/liked")
    public ApiResponse<Boolean> isLiked(HttpServletRequest request, @PathVariable Long postId) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            boolean liked = likeService.isPostLikedByUser(username, postId);
            return ApiResponse.success(liked);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/heatmap")
    public ApiResponse<List<WeightedLatLngDTO>> getHeatMapData() {
        try {
            List<WeightedLatLngDTO> data = postService.getHeatMapData();
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public ApiResponse<Post> getPost(HttpServletRequest request, @PathVariable Long postId) {
        String username = (String) request.getAttribute("username");
        try {
            Post post = postService.getPostById(postId);
            if (username != null) {
                browseHistoryService.recordBrowse(username, postId);
            }
            return ApiResponse.success(post);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(HttpServletRequest request, @PathVariable Long postId) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            postService.deletePost(username, postId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/official")
    public ApiResponse<List<Post>> getOfficialPosts(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // 修复：使用注入的 postRepository，并只返回 type='official' 且 reviewStatus='approved' 的帖子
        List<Post> posts = postRepository.findByTypeAndReviewStatusOrderByCreatedAtDesc("official", "approved", pageable);
        return ApiResponse.success(posts);
    }
}