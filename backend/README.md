# FashionStore Microservices

Website bán quần áo thời trang trực tuyến theo kiến trúc Microservices sử dụng Spring Boot, Spring Cloud, ReactJS, MySQL, Redis, Kafka và Docker.

## Kiến trúc

```
React Frontend (port 3000)
        |
   API Gateway (8080) --- JWT Filter, Routing, Rate Limit
        |
  Eureka Server (8761) --- Service Discovery
        |
  --------------------------------------------------
  | User(8081) | Product(8082) | Cart(8083) | Order(8084) | Payment(8085) | Notification(8086) |
  --------------------------------------------------
     MySQL         MySQL          Redis        MySQL          MySQL         Kafka Consumer
                    +Redis                     +Kafka Producer
```

## Công nghệ

- Backend: Spring Boot 3.2, Spring Cloud 2023.0.1 (Eureka, Gateway, OpenFeign, Resilience4j Circuit Breaker)
- Frontend: ReactJS 18 + React Router + Axios
- Database: MySQL 8 (mỗi service 1 database riêng), Redis (cache + giỏ hàng)
- Message Queue: Apache Kafka (thông báo bất đồng bộ sau khi đặt hàng)
- Authentication: Spring Security + JWT (sinh và validate tại User Service, xác thực lại tại Gateway)
- Containerization: Docker & Docker Compose

## Cách chạy toàn bộ hệ thống bằng Docker Compose

Yêu cầu: Docker & Docker Compose đã cài đặt, máy có kết nối Internet để tải các dependency Maven/npm lần đầu build.

```bash
cd fashionstore
docker-compose up --build
```

Lần build đầu tiên có thể mất 10-20 phút vì mỗi service Java cần tải dependency Maven bên trong container.

Sau khi tất cả container khởi động xong:

| Thành phần         | URL                              |
|--------------------|-----------------------------------|
| Frontend (React)   | http://localhost:3000             |
| API Gateway        | http://localhost:8080             |
| Eureka Dashboard   | http://localhost:8761             |
| User Service       | http://localhost:8081             |
| Product Service    | http://localhost:8082             |
| Cart Service       | http://localhost:8083             |
| Order Service      | http://localhost:8084             |
| Payment Service    | http://localhost:8085             |
| Notification Service | http://localhost:8086           |
| Swagger UI (mỗi service) | http://localhost:PORT/swagger-ui.html |

## Chạy từng service riêng lẻ (development, không dùng Docker)

1. Cài MySQL, Redis, Kafka chạy trên máy local (hoặc `docker-compose up mysql redis kafka zookeeper` trước).
2. Sửa `application.yml` của từng service, đổi các host `mysql`, `redis`, `kafka`, `eureka-server` thành `localhost`.
3. Chạy lần lượt (thứ tự quan trọng):
   ```bash
   cd eureka-server && mvn spring-boot:run
   cd api-gateway && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   cd cart-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```
4. Chạy frontend:
   ```bash
   cd frontend
   npm install
   npm start
   ```
   Frontend sẽ chạy ở http://localhost:3000, gọi API qua http://localhost:8080/api (cấu hình trong file `.env`).

## Tài khoản mặc định

Chưa có tài khoản admin mặc định. Sau khi đăng ký tài khoản đầu tiên qua giao diện (sẽ có role USER), bạn cần vào MySQL và cập nhật thủ công role thành ADMIN để truy cập trang quản trị:

```sql
USE fashionstore_user;
UPDATE users SET role = 'ADMIN' WHERE username = 'ten_dang_nhap_cua_ban';
```

## Luồng nghiệp vụ chính (đặt hàng)

1. Người dùng đăng nhập (JWT được cấp bởi User Service, gửi kèm theo mỗi request).
2. Xem sản phẩm / tìm kiếm theo tên, thương hiệu, danh mục (Product Service, cache Redis).
3. Chọn size, màu, thêm vào giỏ hàng (Cart Service lưu trên Redis, gọi Product Service qua OpenFeign lấy giá/tên/tồn kho).
4. Đặt hàng (Order Service):
   - Kiểm tra tồn kho từng sản phẩm qua Product Service (OpenFeign + Circuit Breaker Resilience4j).
   - Lấy thông tin người mua qua User Service (OpenFeign).
   - Lưu đơn hàng vào MySQL (kèm size/màu đã chọn).
   - Gọi Payment Service xử lý thanh toán (OpenFeign).
   - Trừ tồn kho ở Product Service.
   - Phát sự kiện `OrderCreatedEvent` lên Kafka topic `order-events`.
5. Notification Service lắng nghe Kafka, gửi email xác nhận đơn hàng.

## Lưu ý cấu hình email (Notification Service)

Mặc định `notification-service/src/main/resources/application.yml` dùng SMTP Gmail placeholder. Để gửi email thật, cập nhật:
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password   # dùng App Password của Gmail, không dùng mật khẩu thường
```
Nếu không cấu hình, hệ thống vẫn hoạt động bình thường — lỗi gửi mail chỉ được log lại, không làm gián đoạn luồng đặt hàng.

## Cấu trúc thư mục

```
fashionstore/
├── docker-compose.yml
├── eureka-server/
├── api-gateway/
├── user-service/
├── product-service/
├── cart-service/
├── order-service/
├── payment-service/
├── notification-service/
└── frontend/
```

## Mô hình sản phẩm (Product)

Mỗi sản phẩm quần áo gồm: tên, mô tả, giá, ảnh, tồn kho, mã SKU, danh mục (Category: Áo thun, Quần jean, Váy, ...), thương hiệu (Brand), size có sẵn (ví dụ "S,M,L,XL"), màu có sẵn, chất liệu và đối tượng sử dụng (Nam/Nữ/Unisex/Trẻ em). Khi thêm vào giỏ hàng hoặc đặt hàng, khách chọn 1 size và 1 màu cụ thể cho sản phẩm đó.
