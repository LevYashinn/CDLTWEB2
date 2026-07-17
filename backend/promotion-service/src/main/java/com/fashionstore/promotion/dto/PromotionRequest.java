package com.fashionstore.promotion.dto;

import com.fashionstore.promotion.entity.PromotionScope;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionRequest {

    @NotBlank(message = "Tên chương trình khuyến mãi không được để trống")
    private String name;

    /** Để trống -> khuyến mãi tự động, không cần mã. */
    private String code;

    @NotNull(message = "Vui lòng chọn phạm vi áp dụng")
    private PromotionScope scope;

    private Long categoryId;
    private Long productId;

    @NotNull(message = "Vui lòng nhập phần trăm giảm giá")
    @Min(value = 1, message = "Phần trăm giảm giá tối thiểu là 1%")
    @Max(value = 100, message = "Phần trăm giảm giá tối đa là 100%")
    private Integer discountPercent;

    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Boolean active;
}
