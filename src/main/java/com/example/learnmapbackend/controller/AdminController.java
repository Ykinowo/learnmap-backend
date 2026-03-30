package com.example.learnmapbackend.controller;

import com.example.learnmapbackend.entity.Post;
import com.example.learnmapbackend.repository.PostRepository;
import com.example.learnmapbackend.service.PostService;
import com.example.learnmapbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    // 登录页面
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // 处理登录
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session) {
        try {
            // 调用已有的登录方法验证用户名密码
            String token = userService.login(username, password);
            // 简单判断：用户名包含 "admin" 即为管理员（你可以根据实际修改）
            if (!username.contains("admin")) {
                return "redirect:/admin/login?error=无权登录";
            }
            // 存储登录信息到 Session
            session.setAttribute("adminToken", token);
            session.setAttribute("adminUsername", username);
            return "redirect:/admin/dashboard";
        } catch (RuntimeException e) {
            return "redirect:/admin/login?error=" + e.getMessage();
        }
    }

    // 仪表盘（首页）
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        return "admin/dashboard";
    }

    // 发布通知页面
    @GetMapping("/publish")
    public String publishPage(HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        return "admin/publish";
    }

    // 处理发布通知
    @PostMapping("/publish")
    public String publishPost(@RequestParam(required = false) String title,
                              @RequestParam String content,
                              @RequestParam String locationName,
                              @RequestParam(defaultValue = "38.020615") Double latitude,
                              @RequestParam(defaultValue = "114.600033") Double longitude,
                              @RequestParam(required = false) String tags,
                              @RequestParam(defaultValue = "false") boolean isAnonymous,
                              @RequestParam(required = false) MultipartFile imageFile,
                              HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        try {
            String username = (String) session.getAttribute("adminUsername");
            String imageUrls = "";
            // 如果有图片，调用上传接口（这里先留空，你可以稍后实现）
            // 可复用已有的 FileController 上传逻辑
            Post post = postService.createPost(
                    username, title, content, locationName, latitude, longitude,
                    tags, isAnonymous, imageUrls, "official"  // 关键：type 设为 official
            );
            return "redirect:/admin/publish?success=true";
        } catch (Exception e) {
            return "redirect:/admin/publish?error=" + e.getMessage();
        }
    }

    // 通知列表页面
    @GetMapping("/posts")
    public String listOfficialPosts(Model model, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        // 获取所有校方通知，按时间倒序
        List<Post> posts = postRepository.findByTypeOrderByCreatedAtDesc("official", PageRequest.of(0, 100));
        model.addAttribute("posts", posts);
        return "admin/list";
    }

    // 删除通知
    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        try {
            String username = (String) session.getAttribute("adminUsername");
            postService.deletePost(username, id); // 复用之前的删除方法
        } catch (Exception e) {
            // 可选：记录错误
        }
        return "redirect:/admin/posts";
    }

    // 编辑页面（可选，简单实现）
    @GetMapping("/posts/edit/{id}")
    public String editPage(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        Post post = postRepository.findById(id).orElse(null);
        model.addAttribute("post", post);
        return "admin/edit";
    }

    // 处理编辑
    @PostMapping("/posts/edit")
    public String updatePost(@RequestParam Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam String locationName,
                             @RequestParam Double latitude,
                             @RequestParam Double longitude,
                             @RequestParam(required = false) String tags,
                             @RequestParam(defaultValue = "false") boolean isAnonymous,
                             HttpSession session) {
        if (session.getAttribute("adminToken") == null) {
            return "redirect:/admin/login";
        }
        try {
            // 简单实现：先查询，再更新字段，保存
            Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("帖子不存在"));
            post.setTitle(title);
            post.setContent(content);
            post.setLocationName(locationName);
            post.setLatitude(latitude);
            post.setLongitude(longitude);
            post.setTags(tags);
            post.setAnonymous(isAnonymous);
            // 注意：类型保持 official
            postRepository.save(post);
            return "redirect:/admin/posts";
        } catch (Exception e) {
            return "redirect:/admin/posts?error=" + e.getMessage();
        }
    }
}