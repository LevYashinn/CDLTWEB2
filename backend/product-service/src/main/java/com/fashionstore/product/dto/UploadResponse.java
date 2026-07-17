package com.fashionstore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    /** Đường dẫn tương đối, ví dụ: /uploads/posts/6f2c...-anh.jpg */
    private String url;
}
