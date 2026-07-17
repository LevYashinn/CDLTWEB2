package com.fashionstore.promotion.repository;

import com.fashionstore.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findAllByOrderByCreatedAtDesc();

    Optional<Promotion> findByCodeIgnoreCase(String code);

    /** Các khuyến mãi tự động (không có mã) đang bật - dùng để tự áp vào giỏ hàng. */
    List<Promotion> findByActiveTrueAndCodeIsNull();

    /**
     * Các mã coupon (có code) đang bật - dùng để chọn 1 mã ngẫu nhiên tặng khách
     * khi đơn hàng đạt giá trị tối thiểu (vd > 2.000.000đ).
     */
    List<Promotion> findByActiveTrueAndCodeIsNotNull();
}
