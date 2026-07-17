package com.fashionstore.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thông tin 1 mã sale đang hoạt động, lấy từ promotion-service để gửi tặng
 * khách hàng qua email khi đơn hàng đạt giá trị tối thiểu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveCouponResponse {
    private String code;
    private Integer discountPercent;
    private String description;
}
