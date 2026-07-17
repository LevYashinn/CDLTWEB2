package com.fashionstore.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/** Published to Kafka topic "order-events" after an order is successfully created (async notification flow). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {
    private Long orderId;
    private Long userId;
    private String userEmail;
    private String receiverName;
    private BigDecimal totalAmount;
    private String status;

    /**
     * Mã sale tặng kèm khi đơn hàng đạt giá trị tối thiểu (vd > 2.000.000đ).
     * null nếu đơn hàng không đủ điều kiện hoặc hiện không có mã nào đang hoạt động.
     */
    private String bonusPromoCode;
    private Integer bonusPromoDiscountPercent;
    private String bonusPromoDescription;
}
