package com.fashionstore.order.controller;

import com.fashionstore.order.dto.CreateOrderRequest;
import com.fashionstore.order.dto.OrderResponse;
import com.fashionstore.order.dto.UpdateStatusRequest;
import com.fashionstore.order.exception.UnauthorizedException;
import com.fashionstore.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("X-User-Id") Long userId,
                                                        @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@RequestHeader("X-User-Id") Long userId,
                                                              @RequestHeader("X-User-Role") String role,
                                                              @RequestParam(required = false) Boolean all) {
        if (Boolean.TRUE.equals(all)) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                throw new UnauthorizedException("Chỉ ADMIN mới có quyền xem tất cả đơn hàng");
            }
            return ResponseEntity.ok(orderService.getAllOrders());
        }
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
                                                        @RequestHeader("X-User-Id") Long userId,
                                                        @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(orderService.getOrderById(id, userId, role));
    }

    @PutMapping("/status")
    public ResponseEntity<OrderResponse> updateStatus(@RequestHeader("X-User-Role") String role,
                                                         @RequestParam Long orderId,
                                                         @RequestBody UpdateStatusRequest request) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Chỉ ADMIN mới có quyền cập nhật trạng thái đơn hàng");
        }
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.getStatus()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(orderService.cancelOrder(id, userId));
    }
}
