package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.entity.BrowseHistory;
import com.example.learnmapbackend.entity.User;
import com.example.learnmapbackend.service.BrowseHistoryService;
import com.example.learnmapbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/browse-history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<List<BrowseHistory>> getBrowseHistory(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ApiResponse.error("未登录");
        }
        try {
            User user = userService.getUserByUsername(username);
            List<BrowseHistory> list = browseHistoryService.getUserBrowseHistory(user.getId());
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}