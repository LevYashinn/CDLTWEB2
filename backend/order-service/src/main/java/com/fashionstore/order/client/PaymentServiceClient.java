package com.fashionstore.order.client;

import com.fashionstore.order.dto.PaymentRequest;
import com.fashionstore.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Synchronous (OpenFeign) communication: Order Service -> Payment Service. */
@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}
