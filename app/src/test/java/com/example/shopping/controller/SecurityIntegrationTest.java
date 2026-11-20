package com.example.shopping.controller;

import com.example.shopping.common.entity.Order;
import com.example.shopping.common.entity.Product;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.repository.OrderRepository;
import com.example.shopping.common.repository.ProductRepository;
import com.example.shopping.common.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证与授权端到端测试。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;

    private User alice;
    private User bob;
    private User admin;
    private Product product;
    private Order bobOrder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });

        alice = saveUser("sec_alice", "alice@test.com", User.Role.USER);
        bob = saveUser("sec_bob", "bob@test.com", User.Role.USER);
        admin = saveUser("sec_admin", "admin@test.com", User.Role.ADMIN);

        product = productRepository.save(new Product(
            "sec-product", "security test", new BigDecimal("10.00"), 100, "test"));
        bobOrder = orderRepository.save(new Order(bob, product, 1));
    }

    @Test
    void shouldRejectAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/orders")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/users/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void userCannotReadAnotherUsersProfile() throws Exception {
        String token = login("sec_alice");

        mockMvc.perform(get("/api/users/" + bob.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void userCannotReadOrChangeAnotherUsersOrder() throws Exception {
        String token = login("sec_alice");

        mockMvc.perform(get("/api/orders/" + bobOrder.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/orders/user/" + bob.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/orders/" + bobOrder.getId() + "/status")
                .param("status", "PAID")
                .header("Authorization", bearer(token)))
            .andExpect(status().isForbidden());
    }

    @Test
    void userCannotManageProducts() throws Exception {
        String token = login("sec_alice");

        mockMvc.perform(post("/api/products")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"hack","description":"x","price":1,"stock":1,"category":"x"}
                    """))
            .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/products/" + product.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isForbidden());
    }

    @Test
    void userCanReadAndCancelOwnOrder() throws Exception {
        Order ownOrder = orderRepository.save(new Order(alice, product, 1));
        String token = login("sec_alice");

        mockMvc.perform(get("/api/orders/" + ownOrder.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(alice.getId()));

        mockMvc.perform(post("/api/orders/" + ownOrder.getId() + "/cancel")
                .header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void adminCanReadUsersAndManageOrders() throws Exception {
        String token = login("sec_admin");

        mockMvc.perform(get("/api/users/" + bob.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("sec_bob"));

        mockMvc.perform(get("/api/orders/" + bobOrder.getId())
                .header("Authorization", bearer(token)))
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/orders/" + bobOrder.getId() + "/status")
                .param("status", "PAID")
                .header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"));

        mockMvc.perform(post("/api/products")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"admin-product","description":"x","price":1,"stock":1,"category":"x"}
                    """))
            .andExpect(status().isCreated());
    }

    @Test
    void healthDetailsRequireAdmin() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.components").doesNotExist());

        String token = login("sec_admin");
        mockMvc.perform(get("/actuator/health")
                .header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.components").exists());
    }

    private User saveUser(String username, String email, User.Role role) {
        User user = new User(username, email, passwordEncoder.encode("password123"));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String login(String username) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"password123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        String token = json.get("token").asText();
        assertNotNull(token);
        return token;
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
