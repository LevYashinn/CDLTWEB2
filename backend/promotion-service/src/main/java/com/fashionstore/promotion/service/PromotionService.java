package com.fashionstore.promotion.service;

import com.fashionstore.promotion.dto.*;
import com.fashionstore.promotion.entity.Promotion;
import com.fashionstore.promotion.entity.PromotionScope;
import com.fashionstore.promotion.exception.ResourceNotFoundException;
import com.fashionstore.promotion.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAllByOrderByCreatedAtDesc();
    }

    public Promotion getById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi với id = " + id));
    }

    @Transactional
    public Promotion createPromotion(PromotionRequest request) {
        Promotion promo = Promotion.builder()
                .name(request.getName())
                .code(normalizeCode(request.getCode()))
                .scope(request.getScope())
                .categoryId(request.getCategoryId())
                .productId(request.getProductId())
                .discountPercent(request.getDiscountPercent())
                .description(request.getDescription())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return promotionRepository.save(promo);
    }

    @Transactional
    public Promotion updatePromotion(Long id, PromotionRequest request) {
        Promotion promo = getById(id);
        promo.setName(request.getName());
        promo.setCode(normalizeCode(request.getCode()));
        promo.setScope(request.getScope());
        promo.setCategoryId(request.getCategoryId());
        promo.setProductId(request.getProductId());
        promo.setDiscountPercent(request.getDiscountPercent());
        promo.setDescription(request.getDescription());
        if (request.getStartDate() != null) promo.setStartDate(request.getStartDate());
        promo.setEndDate(request.getEndDate());
        promo.setUsageLimit(request.getUsageLimit());
        if (request.getActive() != null) promo.setActive(request.getActive());
        return promotionRepository.save(promo);
    }

    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy khuyến mãi với id = " + id);
        }
        promotionRepository.deleteById(id);
    }

    /**
     * Tính giảm giá cho giỏ hàng lúc thanh toán:
     * - Tự động áp các khuyến mãi không cần mã (theo sản phẩm/danh mục/toàn đơn).
     * - Nếu có nhập mã coupon hợp lệ, cộng thêm phần giảm của mã đó (theo đúng phạm vi của mã).
     */
    public CalculateCartResponse calculateCart(CalculateCartRequest request) {
        List<CartItemDto> items = request.getItems() != null ? request.getItems() : List.of();

        BigDecimal subtotal = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ---- 1. Khuyến mãi tự động (không cần mã) ----
        List<Promotion> autoPromotions = promotionRepository.findByActiveTrueAndCodeIsNull().stream()
                .filter(this::isCurrentlyValid)
                .toList();

        BigDecimal autoDiscount = BigDecimal.ZERO;
        for (CartItemDto item : items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            int bestPercent = autoPromotions.stream()
                    .filter(p -> matchesItem(p, item))
                    .mapToInt(Promotion::getDiscountPercent)
                    .max()
                    .orElse(0);
            if (bestPercent > 0) {
                autoDiscount = autoDiscount.add(percentOf(lineTotal, bestPercent));
            }
        }

        // ---- 2. Mã coupon (nếu có nhập) ----
        BigDecimal couponDiscount = BigDecimal.ZERO;
        boolean couponApplied = false;
        String message = null;

        String code = request.getCode();
        if (code != null && !code.isBlank()) {
            Optional<Promotion> found = promotionRepository.findByCodeIgnoreCase(code.trim());
            if (found.isEmpty()) {
                message = "Mã giảm giá không tồn tại.";
            } else {
                Promotion promo = found.get();
                String validationError = validateForRedeem(promo);
                if (validationError != null) {
                    message = validationError;
                } else {
                    BigDecimal matchedTotal = items.stream()
                            .filter(i -> matchesItem(promo, i))
                            .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (matchedTotal.compareTo(BigDecimal.ZERO) <= 0) {
                        message = "Mã giảm giá không áp dụng được cho sản phẩm trong giỏ hàng.";
                    } else {
                        couponDiscount = percentOf(matchedTotal, promo.getDiscountPercent());
                        couponApplied = true;
                        message = "Áp dụng mã \"" + promo.getCode() + "\" thành công, giảm " + promo.getDiscountPercent() + "%.";
                    }
                }
            }
        }

        BigDecimal total = subtotal.subtract(autoDiscount).subtract(couponDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        return CalculateCartResponse.builder()
                .subtotal(subtotal)
                .autoDiscount(autoDiscount)
                .couponDiscount(couponDiscount)
                .total(total)
                .couponApplied(couponApplied)
                .message(message)
                .build();
    }

    /**
     * Gọi sau khi đơn hàng đặt thành công để "dùng" 1 lượt của mã coupon.
     * order-service gọi endpoint này qua Feign sau khi tạo đơn hàng xong.
     */
    @Transactional
    public void redeem(String code) {
        if (code == null || code.isBlank()) return;
        Promotion promo = promotionRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Mã giảm giá không tồn tại: " + code));
        String error = validateForRedeem(promo);
        if (error != null) {
            throw new IllegalStateException(error);
        }
        promo.setUsedCount(promo.getUsedCount() + 1);
        promotionRepository.save(promo);
    }

    /**
     * Lấy ngẫu nhiên 1 mã coupon (có code) đang hoạt động và còn hiệu lực
     * (đúng thời gian, còn lượt sử dụng) - dùng để tặng khách hàng qua email
     * khi đơn hàng đạt giá trị tối thiểu. Trả về Optional.empty() nếu không có mã nào phù hợp.
     */
    public Optional<Promotion> getOneActiveCoupon() {
        List<Promotion> validCoupons = promotionRepository.findByActiveTrueAndCodeIsNotNull().stream()
                .filter(this::isCurrentlyValid)
                .toList();
        if (validCoupons.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = (int) (Math.random() * validCoupons.size());
        return Optional.of(validCoupons.get(randomIndex));
    }

    // ---------------------------------------------------------------------

    private boolean isCurrentlyValid(Promotion promo) {
        if (!Boolean.TRUE.equals(promo.getActive())) return false;
        LocalDateTime now = LocalDateTime.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) return false;
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) return false;
        if (promo.getUsageLimit() != null && promo.getUsedCount() != null
                && promo.getUsedCount() >= promo.getUsageLimit()) return false;
        return true;
    }

    /** Trả về null nếu hợp lệ, hoặc thông báo lỗi nếu không dùng được. */
    private String validateForRedeem(Promotion promo) {
        if (!Boolean.TRUE.equals(promo.getActive())) return "Mã giảm giá này hiện không còn hoạt động.";
        LocalDateTime now = LocalDateTime.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) {
            return "Mã giảm giá chưa tới ngày bắt đầu áp dụng.";
        }
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) {
            return "Mã giảm giá đã hết hạn sử dụng.";
        }
        if (promo.getUsageLimit() != null && promo.getUsedCount() != null
                && promo.getUsedCount() >= promo.getUsageLimit()) {
            return "Mã giảm giá đã hết lượt sử dụng.";
        }
        return null;
    }

    private boolean matchesItem(Promotion promo, CartItemDto item) {
        return switch (promo.getScope()) {
            case ALL -> true;
            case CATEGORY -> promo.getCategoryId() != null && promo.getCategoryId().equals(item.getCategoryId());
            case PRODUCT -> promo.getProductId() != null && promo.getProductId().equals(item.getProductId());
        };
    }

    private BigDecimal percentOf(BigDecimal amount, int percent) {
        return amount.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) return null;
        return code.trim().toUpperCase();
    }
}
