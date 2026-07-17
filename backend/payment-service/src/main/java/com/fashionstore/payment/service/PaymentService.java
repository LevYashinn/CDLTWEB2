package com.fashionstore.payment.service;

import com.fashionstore.payment.dto.PaymentRequest;
import com.fashionstore.payment.dto.PaymentResponse;
import com.fashionstore.payment.entity.Payment;
import com.fashionstore.payment.entity.PaymentStatus;
import com.fashionstore.payment.exception.ResourceNotFoundException;
import com.fashionstore.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simulates a payment gateway:
 * - COD: always marked SUCCESS immediately (money collected on delivery).
 * - ONLINE: simulated SUCCESS (in a real system, this would integrate with VNPay/Momo/Stripe...).
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentStatus status = simulatePayment(request.getMethod());

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(status)
                .build();

        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }

    public PaymentResponse getStatusByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thanh toán cho đơn hàng " + orderId));
        return toResponse(payment);
    }

    private PaymentStatus simulatePayment(String method) {
        if ("COD".equalsIgnoreCase(method)) {
            return PaymentStatus.SUCCESS;
        }
        // Simulated online payment gateway - always succeeds in this demo implementation
        return PaymentStatus.SUCCESS;
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus().name())
                .build();
    }
}
