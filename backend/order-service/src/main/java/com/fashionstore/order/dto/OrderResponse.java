package com.fashionstore.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String paymentMethod;
    private String promoCode;
    private BigDecimal discountAmount;
    private List<OrderDetailResponse> items;
    private LocalDateTime createdAt;
}
