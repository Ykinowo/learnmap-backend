package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.entity.Tag;
import com.example.learnmapbackend.service.AdminTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tags")
public class AdminTagController {

    private static final Logger logger = LoggerFactory.getLogger(AdminTagController.class);

    @Autowired
    private AdminTagService adminTagService;

    // 供App端获取所有标签（按使用次数降序）
    @GetMapping("/all")
    public ApiResponse<List<String>> getAllTags() {
        logger.info("收到获取所有标签请求");
        try {
            // 获取前50个常用标签
            Page<Tag> tagPage = adminTagService.getTags(0, 50, null);
            List<String> tagNames = tagPage.getContent().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            logger.info("返回标签数量：{}", tagNames.size());
            return ApiResponse.success(tagNames);
        } catch (Exception e) {
            logger.error("获取标签失败", e);
            return ApiResponse.error("获取标签失败：" + e.getMessage());
        }
    }

    // 管理端：分页获取标签列表
    @GetMapping
    public ApiResponse<Map<String, Object>> listTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        Page<Tag> tagPage = adminTagService.getTags(page, size, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("tags", tagPage.getContent());
        result.put("totalPages", tagPage.getTotalPages());
        result.put("totalElements", tagPage.getTotalElements());
        result.put("currentPage", page);
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<Tag> addTag(@RequestParam String name, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        try {
            Tag tag = adminTagService.addTag(name);
            return ApiResponse.success(tag);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Tag> updateTag(@PathVariable Long id, @RequestParam String name, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        try {
            Tag tag = adminTagService.updateTag(id, name);
            return ApiResponse.success(tag);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        try {
            adminTagService.deleteTag(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/merge")
    public ApiResponse<Void> mergeTags(@RequestParam Long sourceId, @RequestParam Long targetId, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return ApiResponse.error("未登录");
        }
        try {
            adminTagService.mergeTags(sourceId, targetId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}