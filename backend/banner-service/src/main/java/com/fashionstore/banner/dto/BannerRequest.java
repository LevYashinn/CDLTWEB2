package com.fashionstore.banner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BannerRequest {

    @NotBlank(message = "Tiêu đề banner không được để trống")
    private String title;

    @NotBlank(message = "Ảnh banner không được để trống")
    private String imageUrl;

    private String linkUrl;
    private String description;
    private Integer displayOrder;
    private Boolean active;
}
