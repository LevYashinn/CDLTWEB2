package com.fashionstore.post.service;

import com.fashionstore.post.dto.PostRequest;
import com.fashionstore.post.entity.Post;
import com.fashionstore.post.exception.ResourceNotFoundException;
import com.fashionstore.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /** Dùng cho trang quản trị: TẤT CẢ bài viết (kể cả bản nháp chưa đăng). */
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByPublishedAtDesc();
    }

    /** Dùng cho trang chủ: chỉ bài đã đăng, mới nhất trước, giới hạn số lượng. */
    public List<Post> getLatestPublished(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        return postRepository.findByPublishedTrueOrderByPublishedAtDesc(pageable);
    }

    /** Danh sách đầy đủ bài đã đăng (dùng cho trang "Tin tức" nếu cần sau này). */
    public List<Post> getAllPublished() {
        return postRepository.findByPublishedTrueOrderByPublishedAtDesc();
    }

    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với id = " + id));
    }

    @Transactional
    public Post createPost(PostRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .author(request.getAuthor() != null && !request.getAuthor().isBlank() ? request.getAuthor() : "FashionStore")
                .published(request.getPublished() != null ? request.getPublished() : true)
                .publishedAt(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, PostRequest request) {
        Post post = getById(id);
        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        if (request.getAuthor() != null && !request.getAuthor().isBlank()) post.setAuthor(request.getAuthor());
        if (request.getPublished() != null) post.setPublished(request.getPublished());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bài viết với id = " + id);
        }
        postRepository.deleteById(id);
    }
}
