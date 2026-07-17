package com.fashionstore.order.client;

import com.fashionstore.order.dto.StockCheckResponse;
import com.fashionstore.order.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/** Synchronous (OpenFeign) communication: Order Service -> Product Service, to check stock and deduct it. */
@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}/check-stock")
    StockCheckResponse checkStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @PutMapping("/api/products/stock")
    void updateStock(@RequestBody StockUpdateRequest request);
}
