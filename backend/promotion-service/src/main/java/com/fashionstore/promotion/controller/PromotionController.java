package com.fashionstore.promotion.controller;

import com.fashionstore.promotion.dto.*;
import com.fashionstore.promotion.entity.Promotion;
import com.fashionstore.promotion.exception.UnauthorizedException;
import com.fashionstore.promotion.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService service;

    /** Admin - danh sách tất cả khuyến mãi (kể cả hết hạn/tắt). */
    @GetMapping
    public ResponseEntity<List<Promotion>> getAll(@RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        return ResponseEntity.ok(service.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getById(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Promotion> create(@RequestHeader("X-User-Role") String role,
                                             @Valid @RequestBody PromotionRequest request) {
        requireAdmin(role);
        return ResponseEntity.ok(service.createPromotion(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotion> update(@PathVariable Long id, @RequestHeader("X-User-Role") String role,
                                             @Valid @RequestBody PromotionRequest request) {
        requireAdmin(role);
        return ResponseEntity.ok(service.updatePromotion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        service.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Public - gọi lúc thanh toán: tính tổng tiền sau khi áp khuyến mãi tự động
     * + mã coupon (nếu người dùng có nhập).
     */
    @PostMapping("/calculate")
    public ResponseEntity<CalculateCartResponse> calculate(@RequestBody CalculateCartRequest request) {
        return ResponseEntity.ok(service.calculateCart(request));
    }

    /**
     * Gọi nội bộ (từ order-service) ngay sau khi đơn hàng được tạo thành công,
     * để trừ 1 lượt sử dụng của mã coupon.
     */
    @PostMapping("/redeem")
    public ResponseEntity<Void> redeem(@RequestBody RedeemRequest request) {
        service.redeem(request.getCode());
        return ResponseEntity.ok().build();
    }

    /**
     * Gọi nội bộ (từ order-service) khi đơn hàng đạt giá trị tối thiểu (vd > 2.000.000đ),
     * để lấy 1 mã sale đang hoạt động gửi tặng khách hàng qua email.
     * Trả về 204 No Content nếu hiện không có mã coupon nào đang hoạt động.
     */
    @GetMapping("/active-coupon")
    public ResponseEntity<ActiveCouponResponse> getOneActiveCoupon() {
        return service.getOneActiveCoupon()
                .map(promo -> ResponseEntity.ok(ActiveCouponResponse.builder()
                        .code(promo.getCode())
                        .discountPercent(promo.getDiscountPercent())
                        .description(promo.getDescription())
                        .build()))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Chỉ ADMIN mới có quyền thực hiện thao tác này");
        }
    }
}
