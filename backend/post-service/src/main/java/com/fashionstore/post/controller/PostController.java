package com.fashionstore.post.controller;

import com.fashionstore.post.dto.PostRequest;
import com.fashionstore.post.dto.UploadResponse;
import com.fashionstore.post.entity.Post;
import com.fashionstore.post.exception.UnauthorizedException;
import com.fashionstore.post.service.FileStorageService;
import com.fashionstore.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;
    private final FileStorageService fileStorageService;

    /** Public - trang chủ: 3 bài mới nhất, vd GET /api/posts/latest?limit=3 */
    @GetMapping("/latest")
    public ResponseEntity<List<Post>> getLatest(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(service.getLatestPublished(limit));
    }

    /** Public - danh sách đầy đủ bài đã đăng. */
    @GetMapping
    public ResponseEntity<List<Post>> getAllPublished() {
        return ResponseEntity.ok(service.getAllPublished());
    }

    /** Admin - trang quản trị: tất cả bài viết (kể cả bản nháp). */
    @GetMapping("/admin")
    public ResponseEntity<List<Post>> getAllForAdmin(@RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        return ResponseEntity.ok(service.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Post> create(@RequestHeader("X-User-Role") String role,
                                        @Valid @RequestBody PostRequest request) {
        requireAdmin(role);
        return ResponseEntity.ok(service.createPost(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestHeader("X-User-Role") String role,
                                        @Valid @RequestBody PostRequest request) {
        requireAdmin(role);
        return ResponseEntity.ok(service.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /** Admin - upload ảnh cho bài viết, trả về URL để lưu vào field imageUrl. */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@RequestHeader("X-User-Role") String role,
                                                  @RequestParam("file") MultipartFile file) {
        requireAdmin(role);
        String url = fileStorageService.store(file);
        return ResponseEntity.ok(new UploadResponse(url));
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Chỉ ADMIN mới có quyền thực hiện thao tác này");
        }
    }
}
