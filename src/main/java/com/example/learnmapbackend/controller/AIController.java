package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import com.example.learnmapbackend.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private DeepSeekService deepSeekService;

    @PostMapping("/chat")
    public ApiResponse<String> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.isBlank()) {
            return ApiResponse.error("问题不能为空");
        }
        try {
            String answer = deepSeekService.askQuestion(question);
            return ApiResponse.success(answer);
        } catch (Exception e) {
            return ApiResponse.error("AI 服务出错：" + e.getMessage());
        }
    }
}