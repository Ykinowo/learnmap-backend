package com.example.learnmapbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ZhipuAiService {

    @Value("${zhipu.api.key}")
    private String apiKey;

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)   // 连接超时60秒
            .writeTimeout(60, TimeUnit.SECONDS)     // 写入超时60秒
            .readTimeout(120, TimeUnit.SECONDS)     // 读取超时120秒（智谱生成复杂问题可能需要较长时间）
            .build();    private final ObjectMapper mapper = new ObjectMapper();

    public String askQuestion(String question) throws IOException {
        // 构建请求体（智谱 GLM-4 格式）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4");
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", question)
        });
        requestBody.put("temperature", 0.7);
        requestBody.put("top_p", 0.9);
        requestBody.put("stream", false);  // 不使用流式

        String json = mapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("智谱 AI 请求失败，状态码：" + response.code() + "，信息：" + response.message());
            }
            String responseBody = response.body().string();
            JsonNode root = mapper.readTree(responseBody);
            // 智谱的返回结构：choices[0].message.content
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }
}