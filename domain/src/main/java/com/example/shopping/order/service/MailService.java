package com.example.shopping.order.service;

import com.example.shopping.common.entity.Order;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件服务类
 * <p>
 * 提供异步邮件发送功能，用于发送订单确认和状态更新通知。
 * 当邮件服务器不可用时，会模拟发送（仅记录日志）。
 * </p>
 */
@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    /**
     * 创建邮件服务
     *
     * @param mailSender JavaMailSender（可选，为 null 时使用模拟模式）
     */
    public MailService(@Nullable JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 异步发送订单确认邮件
     *
     * @param order 订单实体
     */
    @Async("mailExecutor")
    public void sendOrderConfirmation(Order order) {
        String to = order.getUser().getEmail();
        String subject = "订单确认 - 订单号: " + order.getId();
        String content = buildOrderEmailContent(order);

        sendEmail(to, subject, content);
    }

    /**
     * 异步发送订单状态更新邮件
     *
     * @param order 订单实体
     */
    @Async("mailExecutor")
    public void sendOrderStatusUpdate(Order order) {
        String to = order.getUser().getEmail();
        String subject = "订单状态更新 - 订单号: " + order.getId();
        String content = buildStatusUpdateEmailContent(order);

        sendEmail(to, subject, content);
    }

    /**
     * 发送邮件
     * <p>
     * 如果邮件服务器不可用，则模拟发送（记录日志）。
     * </p>
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容（HTML格式）
     */
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

    /**
     * 构建订单确认邮件内容
     *
     * @param order 订单实体
     * @return HTML 格式的邮件内容
     */
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

    /**
     * 构建订单状态更新邮件内容
     *
     * @param order 订单实体
     * @return HTML 格式的邮件内容
     */
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