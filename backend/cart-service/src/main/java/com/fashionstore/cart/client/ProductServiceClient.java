package com.fashionstore.cart.client;

import com.fashionstore.cart.dto.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Synchronous call to Product Service (via Eureka + OpenFeign) to fetch price/stock/name when adding to cart. */
@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    ProductInfo getProductById(@PathVariable("id") Long id);
}
