package com.fashionstore.order.client;

import com.fashionstore.order.dto.ActiveCouponResponse;
import com.fashionstore.order.dto.CalculatePromoRequest;
import com.fashionstore.order.dto.CalculatePromoResponse;
import com.fashionstore.order.dto.RedeemPromoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Synchronous (OpenFeign) communication: Order Service -> Promotion Service, to apply/redeem discounts. */
@FeignClient(name = "promotion-service")
public interface PromotionServiceClient {

    @PostMapping("/api/promotions/calculate")
    CalculatePromoResponse calculate(@RequestBody CalculatePromoRequest request);

    @PostMapping("/api/promotions/redeem")
    void redeem(@RequestBody RedeemPromoRequest request);

    /** Lấy 1 mã sale đang hoạt động - trả về 204 (body null) nếu hiện không có mã nào. */
    @GetMapping("/api/promotions/active-coupon")
    ResponseEntity<ActiveCouponResponse> getOneActiveCoupon();
}
