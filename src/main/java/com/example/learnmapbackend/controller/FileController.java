package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.controller.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件为空");
        }
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 保存到本地
            File dest = new File(uploadPath, newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            // 返回可访问的 URL（这里假设静态资源映射到 /uploads/）
            String url = "/uploads/" + newFileName;
            return ApiResponse.success(url);
        } catch (IOException e) {
            return ApiResponse.error("上传失败：" + e.getMessage());
        }
    }
}