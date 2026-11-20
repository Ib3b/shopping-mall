package com.example.shopping.web.adapter;

import com.example.shopping.order.event.OrderNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MailNotificationAdapterTest {

    private MailNotificationAdapter mailNotificationAdapter;

    private OrderNotification notification;

    @BeforeEach
    void setUp() {
        mailNotificationAdapter = new MailNotificationAdapter(null);
        notification = new OrderNotification(
            1L, 1L, "testuser", "test@example.com",
            1L, "测试商品", 2, new BigDecimal("200.00"),
            "PENDING", "待支付"
        );
    }

    @Test
    void shouldSendOrderConfirmation() {
        assertDoesNotThrow(() -> mailNotificationAdapter.sendOrderConfirmation(notification));
    }

    @Test
    void shouldSendOrderStatusUpdate() {
        assertDoesNotThrow(() -> mailNotificationAdapter.sendOrderStatusUpdate(notification));
    }

    @Test
    void shouldEscapeUserControlledFieldsInEmailHtml() {
        OrderNotification malicious = new OrderNotification(
            2L, 2L, "<script>alert(1)</script>", "x@example.com",
            2L, "<img src=x onerror=alert(2)>", 1, new BigDecimal("1.00"),
            "PENDING", "待支付"
        );

        String content = mailNotificationAdapter.buildOrderEmailContent(malicious);

        assertTrue(content.contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
        assertTrue(content.contains("&lt;img src=x onerror=alert(2)&gt;"));
        assertFalse(content.contains("<script>alert(1)</script>"));
    }
}
