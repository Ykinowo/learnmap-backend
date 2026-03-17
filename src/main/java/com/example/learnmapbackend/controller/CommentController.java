package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.controller.dto.CommentRequest;
import com.example.learnmapbackend.entity.Comment;
import com.example.learnmapbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/post/{postId}")
    public ApiResponse<Comment> addComment(HttpServletRequest request,
                                           @PathVariable Long postId,
                                           @RequestBody CommentRequest commentRequest) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            Comment comment = commentService.addComment(username, postId, commentRequest.getContent(), commentRequest.getParentId());
            return ApiResponse.success(comment);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<List<Comment>> getComments(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPost(postId);
            return ApiResponse.success(comments);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}