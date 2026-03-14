package com.example.shopping.repository;

import com.example.shopping.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldSaveAndFindUser() {
        User user = new User("testuser", "test@example.com", "password123");
        User saved = userRepository.save(user);
        
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
        assertEquals("test@example.com", saved.getEmail());
    }
    
    @Test
    void shouldFindByUsername() {
        User user = new User("findme", "findme@example.com", "password");
        userRepository.save(user);
        
        var found = userRepository.findByUsername("findme");
        assertTrue(found.isPresent());
        assertEquals("findme", found.get().getUsername());
    }
    
    @Test
    void shouldFindByEmail() {
        User user = new User("emailuser", "unique@example.com", "password");
        userRepository.save(user);
        
        var found = userRepository.findByEmail("unique@example.com");
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getEmail());
    }
    
    @Test
    void shouldCheckUsernameExists() {
        User user = new User("existing", "existing@example.com", "password");
        userRepository.save(user);
        
        assertTrue(userRepository.existsByUsername("existing"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }
    
    @Test
    void shouldCheckEmailExists() {
        User user = new User("emailtest", "exists@example.com", "password");
        userRepository.save(user);
        
        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("notexists@example.com"));
    }
}