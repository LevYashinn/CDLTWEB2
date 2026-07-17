package com.fashionstore.promotion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * Mã coupon khách phải tự nhập lúc thanh toán (vd "SALE20").
     * Để trống (null) => đây là khuyến mãi TỰ ĐỘNG áp dụng, không cần nhập mã.
     */
    @Column(unique = true, length = 50)
    private String code;

    /** Phạm vi áp dụng: toàn bộ đơn hàng, theo danh mục, hoặc theo 1 sản phẩm cụ thể. */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PromotionScope scope = PromotionScope.ALL;

    /** Chỉ có giá trị khi scope = CATEGORY. */
    private Long categoryId;

    /** Chỉ có giá trị khi scope = PRODUCT. */
    private Long productId;

    /** Phần trăm giảm giá, 1-100. */
    @Column(nullable = false)
    private Integer discountPercent;

    private String description;

    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime endDate;

    /** null = không giới hạn số lần dùng. */
    private Integer usageLimit;

    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** true nếu cần khách nhập mã, false nếu tự động áp dụng. */
    @Transient
    public boolean requiresCode() {
        return code != null && !code.isBlank();
    }
}
