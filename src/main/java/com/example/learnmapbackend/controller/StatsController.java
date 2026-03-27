package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.controller.dto.LocationStatDTO;
import com.example.learnmapbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private PostService postService;

    @GetMapping("/tags")
    public ApiResponse<Map<String, Integer>> getTagStats() {
        try {
            Map<String, Integer> stats = postService.getTagStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/locations")
    public ApiResponse<List<LocationStatDTO>> getLocationStats(@RequestParam(defaultValue = "3") int top) {
        try {
            List<LocationStatDTO> stats = postService.getLocationStats(top);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}