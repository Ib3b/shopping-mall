package com.example.shopping.config;

import com.example.shopping.common.entity.User;
import com.example.shopping.common.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DataInitializerTest {

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldEncodePlaintextPasswordsOnFirstRun() {
        userRepository.save(new User("plainuser", "plain@example.com", "rawpassword"));

        dataInitializer.run();

        User saved = userRepository.findByUsername("plainuser").orElseThrow();
        assertTrue(saved.getPassword().startsWith("$2a$"), "Password should be BCrypt-encoded");
        assertTrue(passwordEncoder.matches("rawpassword", saved.getPassword()),
                "BCrypt hash should match original password");
    }

    @Test
    void shouldNotReEncodeAlreadyBCryptPasswords() {
        String onceEncoded = passwordEncoder.encode("onetime");
        userRepository.save(new User("testuser", "test@example.com", onceEncoded));

        dataInitializer.run();

        User saved = userRepository.findByUsername("testuser").orElseThrow();
        assertEquals(onceEncoded, saved.getPassword(),
                "Already BCrypt-encoded password should not be modified");
    }

    @Test
    void shouldBeIdempotent() {
        userRepository.save(new User("idemuser", "idem@example.com", "rawpassword"));

        dataInitializer.run();
        dataInitializer.run();

        List<User> all = userRepository.findAll();
        assertEquals(1, all.size(), "No duplicate users should be created");
        assertTrue(all.get(0).getPassword().startsWith("$2a$"));
        assertTrue(passwordEncoder.matches("rawpassword", all.get(0).getPassword()));
    }

    @Test
    void shouldSkipWhenNoPlaintextPasswords() {
        String encoded = passwordEncoder.encode("pwd");
        userRepository.save(new User("encuser", "enc@example.com", encoded));

        dataInitializer.run();

        assertEquals(1, userRepository.count());
    }

    @Test
    void shouldNotFailWithEmptyDatabase() {
        assertDoesNotThrow(() -> dataInitializer.run());
    }
}
