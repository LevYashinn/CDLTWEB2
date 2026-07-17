package com.fashionstore.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    private String title;

    private String summary;

    @NotBlank(message = "Nội dung bài viết không được để trống")
    private String content;

    private String imageUrl;
    private String author;
    private Boolean published;
}
