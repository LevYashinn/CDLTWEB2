package com.fashionstore.cart.controller;

import com.fashionstore.cart.dto.AddToCartRequest;
import com.fashionstore.cart.dto.CartResponse;
import com.fashionstore.cart.dto.UpdateCartRequest;
import com.fashionstore.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCart(@RequestHeader("X-User-Id") Long userId,
                                                     @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, request));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> removeItem(@RequestHeader("X-User-Id") Long userId,
                                                     @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
