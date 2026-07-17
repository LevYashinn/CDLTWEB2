package com.fashionstore.product.config;

/**
 * Đã tắt cache Redis cho product-service.
 *
 * Trước đây ProductService dùng @Cacheable/@CacheEvict để cache danh sách/chi tiết
 * sản phẩm vào Redis, liên tục gây lỗi serialize/deserialize mỗi khi cấu trúc
 * ProductResponse thay đổi (thêm field mới) mà Redis vẫn còn dữ liệu cache cũ
 * không tương thích - gây lỗi 500 lan sang cả cart-service/order-service (vì các
 * service đó phải gọi API này để lấy thông tin sản phẩm). Vì cache chỉ tăng tốc độ
 * đọc chứ không bắt buộc, ta bỏ hẳn để hệ thống chạy ổn định.
 */
public class CacheConfig {
}
