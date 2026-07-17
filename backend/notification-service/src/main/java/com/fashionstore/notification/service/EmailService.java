package com.fashionstore.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, Long orderId, String receiverName, String amount) {
        sendOrderConfirmation(toEmail, orderId, receiverName, amount, null, null, null);
    }

    /**
     * Gửi email xác nhận đơn hàng, kèm 1 mã sale tặng khách nếu đơn hàng đạt giá trị tối thiểu
     * (bonusPromoCode khác null - mục "tặng mã sale cho đơn hàng trên 2.000.000đ").
     */
    public void sendOrderConfirmation(String toEmail, Long orderId, String receiverName, String amount,
                                       String bonusPromoCode, Integer bonusPromoDiscountPercent,
                                       String bonusPromoDescription) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Xác nhận đơn hàng #" + orderId + " - FashionStore");

            StringBuilder body = new StringBuilder()
                    .append("Xin chào ").append(receiverName).append(",\n\n")
                    .append("Cảm ơn bạn đã đặt hàng tại FashionStore.\n")
                    .append("Mã đơn hàng: #").append(orderId).append("\n")
                    .append("Tổng tiền: ").append(amount).append(" VNĐ\n\n")
                    .append("Chúng tôi sẽ xử lý và giao hàng trong thời gian sớm nhất.\n");

            if (bonusPromoCode != null && !bonusPromoCode.isBlank()) {
                body.append("\n----------------------------------------\n")
                        .append("🎁 Quà tặng đặc biệt dành cho bạn!\n")
                        .append("Đơn hàng của bạn đạt giá trị trên 2.000.000đ, FashionStore xin gửi tặng bạn 1 mã giảm giá cho lần mua sắm tiếp theo:\n\n")
                        .append("Mã: ").append(bonusPromoCode).append("\n");
                if (bonusPromoDiscountPercent != null) {
                    body.append("Ưu đãi: giảm ").append(bonusPromoDiscountPercent).append("%\n");
                }
                if (bonusPromoDescription != null && !bonusPromoDescription.isBlank()) {
                    body.append("Mô tả: ").append(bonusPromoDescription).append("\n");
                }
                body.append("----------------------------------------\n");
            }

            body.append("\nTrân trọng,\nFashionStore Team");
            message.setText(body.toString());

            mailSender.send(message);
            log.info("Order confirmation email sent to {} for order #{}{}", toEmail, orderId,
                    bonusPromoCode != null ? " (kèm mã sale " + bonusPromoCode + ")" : "");
        } catch (Exception e) {
            // Do not fail the notification flow if the mail server (e.g. SMTP) is not configured
            log.warn("Could not send email for order #{}: {}", orderId, e.getMessage());
        }
    }
}
