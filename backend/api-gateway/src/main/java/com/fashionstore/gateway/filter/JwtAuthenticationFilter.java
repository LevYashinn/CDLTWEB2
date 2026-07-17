package com.fashionstore.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final String SECRET = "fashionstore-super-secret-key-please-change-in-production-0123456789";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // CHỈ để các API xác thực ở đây (để nó luôn public với mọi Method)
    private static final List<String> PUBLIC_AUTH_ENDPOINTS = List.of(
            "/api/users/register",
            "/api/users/login");

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Bỏ qua kiểm tra Token đối với các request OPTIONS từ trình duyệt (CORS)
            if (request.getMethod().name().equals("OPTIONS")) {
                return chain.filter(exchange);
            }

            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            // Kiểm tra xem có phải API login/register không
            boolean isPublic = PUBLIC_AUTH_ENDPOINTS.stream().anyMatch(path::startsWith);

            // CHỈ cho phép GET (xem dữ liệu) đối với products và categories là public
            if (method.equals("GET") && (path.startsWith("/api/products") || path.startsWith("/api/categories"))) {
                isPublic = true;
            }

            // Danh sách banner đang bật hiển thị ở trang chủ - không cần đăng nhập
            if (method.equals("GET") && path.equals("/api/banners")) {
                isPublic = true;
            }

            // Bài viết: danh sách công khai, "mới nhất", và xem chi tiết 1 bài - không cần đăng nhập
            // (chỉ /api/posts/admin mới cần quyền admin, được xử lý riêng trong PostController)
            if (method.equals("GET") && path.startsWith("/api/posts") && !path.startsWith("/api/posts/admin")) {
                isPublic = true;
            }

            // Nếu là public thì cho đi thẳng, không cần xét Token
            if (isPublic) {
                return chain.filter(exchange);
            }

            // Bắt đầu xử lý Token cho các API cần bảo mật (như POST/PUT/DELETE danh mục)
            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst("Authorization");
            String token = (authHeader != null && authHeader.startsWith("Bearer "))
                    ? authHeader.substring(7)
                    : null;

            if (token == null) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build()
                        .parseClaimsJws(token).getBody();

                // Đính kèm ID và Role vào Header để gửi xuống Backend
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", claims.getSubject())
                        .header("X-User-Role", String.valueOf(claims.get("role")))
                        .build();

                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                return chain.filter(mutatedExchange);
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private reactor.core.publisher.Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}