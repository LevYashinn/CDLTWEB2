package com.fashionstore.post.repository;

import com.fashionstore.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByPublishedAtDesc();

    List<Post> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    List<Post> findByPublishedTrueOrderByPublishedAtDesc();
}
