package com.fashionstore.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/** Mirrors the event published by Order Service to the "order-events" Kafka topic. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {
    private Long orderId;
    private Long userId;
    private String userEmail;
    private String receiverName;
    private BigDecimal totalAmount;
    private String status;

    /** Mã sale tặng kèm khi đơn hàng đạt giá trị tối thiểu (vd > 2.000.000đ). Null nếu không có. */
    private String bonusPromoCode;
    private Integer bonusPromoDiscountPercent;
    private String bonusPromoDescription;
}
