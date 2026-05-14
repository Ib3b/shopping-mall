package com.example.shopping.web.adapter;

import com.example.shopping.common.entity.Order;
import com.example.shopping.order.port.NotificationSender;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 邮件通知适配器
 * <p>
 * 实现 {@link NotificationSender} 接口，通过 JavaMailSender 发送邮件通知。
 * 当邮件服务器不可用时，会模拟发送（仅记录日志）。
 * 这是基础设施层的实现，领域层通过接口依赖不直接感知具体实现。
 * </p>
 */
@Component
public class MailNotificationAdapter implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(MailNotificationAdapter.class);

    private final JavaMailSender mailSender;

    public MailNotificationAdapter(@Nullable JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOrderConfirmation(Order order) {
        String to = order.getUser().getEmail();
        String subject = "订单确认 - 订单号: " + order.getId();
        String content = buildOrderEmailContent(order);

        sendEmail(to, subject, content);
    }

    @Override
    public void sendOrderStatusUpdate(Order order) {
        String to = order.getUser().getEmail();
        String subject = "订单状态更新 - 订单号: " + order.getId();
        String content = buildStatusUpdateEmailContent(order);

        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            if (mailSender == null) {
                logger.info("=== 模拟发送邮件 ===");
                logger.info("收件人: {}", to);
                logger.info("主题: {}", subject);
                logger.info("内容: \n{}", content);
                logger.info("=====================");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            logger.info("邮件发送成功: {}", to);

        } catch (Exception e) {
            logger.warn("邮件发送失败: {}, 错误: {}", to, e.getMessage());
        }
    }

    private String buildOrderEmailContent(Order order) {
        return String.format("""
            <html>
            <body>
                <h2>订单确认</h2>
                <p>尊敬的 %s，您好！</p>
                <p>您的订单已创建成功，订单详情如下：</p>
                <table border="1" cellpadding="5">
                    <tr><td>订单编号</td><td>%d</td></tr>
                    <tr><td>商品名称</td><td>%s</td></tr>
                    <tr><td>购买数量</td><td>%d</td></tr>
                    <tr><td>订单金额</td><td>¥%.2f</td></tr>
                    <tr><td>订单状态</td><td>%s</td></tr>
                </table>
                <p>感谢您的购买！</p>
            </body>
            </html>
            """,
            order.getUser().getUsername(),
            order.getId(),
            order.getProduct().getName(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus().getDescription()
        );
    }

    private String buildStatusUpdateEmailContent(Order order) {
        return String.format("""
            <html>
            <body>
                <h2>订单状态更新</h2>
                <p>尊敬的 %s，您好！</p>
                <p>您的订单状态已更新：</p>
                <table border="1" cellpadding="5">
                    <tr><td>订单编号</td><td>%d</td></tr>
                    <tr><td>当前状态</td><td>%s</td></tr>
                </table>
                <p>请关注您的订单状态。</p>
            </body>
            </html>
            """,
            order.getUser().getUsername(),
            order.getId(),
            order.getStatus().getDescription()
        );
    }
}
