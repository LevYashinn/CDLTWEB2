package com.fashionstore.order.client;

import com.fashionstore.order.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Synchronous (OpenFeign) communication: Order Service -> User Service, to fetch buyer information. */
@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserInfo getUserById(@PathVariable("id") Long id);
}
