package com.fashionstore.cart.service;

import com.fashionstore.cart.client.ProductServiceClient;
import com.fashionstore.cart.dto.AddToCartRequest;
import com.fashionstore.cart.dto.ProductInfo;
import com.fashionstore.cart.dto.UpdateCartRequest;
import com.fashionstore.cart.dto.CartResponse;
import com.fashionstore.cart.model.CartItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Cart is stored entirely in Redis: key = "cart:{userId}", each field =
 * productId, value = CartItem (JSON).
 * No relational database is used for the cart, per the microservices design
 * (Cart Service -> Redis only).
 */
@Service
@RequiredArgsConstructor
@Slf4j // Thêm cái này để ghi log nếu gọi API thất bại
public class CartService {

    private final HashOperations<String, String, Object> hashOperations;
    private final ProductServiceClient productServiceClient;

    private String key(Long userId) {
        return "cart:" + userId;
    }

    public CartResponse getCart(Long userId) {
        Collection<Object> values = hashOperations.values(key(userId));
        List<CartItem> items = values.stream().map(v -> (CartItem) v).toList();

        // =====================================================================
        // 👇 TÍNH NĂNG MỚI: LẤY SỐ LƯỢNG TỒN KHO THỰC TẾ TỪ PRODUCT SERVICE 👇
        // =====================================================================
        for (CartItem item : items) {
            try {
                // Gọi API sang Product Service để lấy thông tin mới nhất của sản phẩm
                ProductInfo product = productServiceClient.getProductById(item.getProductId());
                if (product != null) {
                    item.setStock(product.getStock()); // Bơm tồn kho thật vào biến stock
                }
            } catch (Exception e) {
                log.warn("Không thể lấy tồn kho cho sản phẩm {}: {}", item.getProductId(), e.getMessage());
                // Nếu Product Service bị sập hoặc sản phẩm đã bị xóa, ép tồn kho về 0 để khóa
                // thanh toán
                item.setStock(0);
            }
        }
        // =====================================================================

        return buildResponse(items);
    }

    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        ProductInfo product = productServiceClient.getProductById(request.getProductId());
        String field = String.valueOf(request.getProductId());

        CartItem existing = (CartItem) hashOperations.get(key(userId), field);
        int newQuantity = request.getQuantity() != null ? request.getQuantity() : 1;
        if (existing != null) {
            newQuantity += existing.getQuantity();
        }

        CartItem item = CartItem.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(newQuantity)
                .imageUrl(product.getImageUrl())
                .size(request.getSize())
                .color(request.getColor())
                .categoryId(product.getCategoryId())
                .build();

        hashOperations.put(key(userId), field, item);
        return getCart(userId);
    }

    public CartResponse updateQuantity(Long userId, UpdateCartRequest request) {
        String field = String.valueOf(request.getProductId());
        CartItem existing = (CartItem) hashOperations.get(key(userId), field);
        if (existing == null) {
            return getCart(userId);
        }
        if (request.getQuantity() <= 0) {
            hashOperations.delete(key(userId), field);
        } else {
            existing.setQuantity(request.getQuantity());
            hashOperations.put(key(userId), field, existing);
        }
        return getCart(userId);
    }

    public CartResponse removeItem(Long userId, Long productId) {
        hashOperations.delete(key(userId), String.valueOf(productId));
        return getCart(userId);
    }

    public void clearCart(Long userId) {
        hashOperations.getOperations().delete(key(userId));
    }

    private CartResponse buildResponse(List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        return CartResponse.builder().items(items).totalAmount(total).totalItems(totalItems).build();
    }
}