package com.example.shopping.config;

import com.example.shopping.common.entity.User;
import com.example.shopping.common.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        entityManager.createNativeQuery(
                "UPDATE user_info SET version = 0 WHERE version IS NULL")
            .executeUpdate();
        entityManager.clear();

        int updated = 0;
        for (User user : userRepository.findAll()) {
            String password = user.getPassword();
            if (!password.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(password));
                updated++;
            }
        }
        if (updated > 0) {
            logger.info("已迁移 {} 个用户的密码为 BCrypt 加密", updated);
        }
    }
}
