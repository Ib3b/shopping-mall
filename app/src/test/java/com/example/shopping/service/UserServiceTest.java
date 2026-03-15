package com.example.shopping.service;

import com.example.shopping.common.dto.UserRequest;
import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.common.repository.UserRepository;
import com.example.shopping.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "encodedPassword");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UserRequest request = new UserRequest("newuser", "new@example.com", "password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("newuser", response.username());
        assertEquals("new@example.com", response.email());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {
        UserRequest request = new UserRequest("existing", "new@example.com", "password");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.createUser(request));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        UserRequest request = new UserRequest("newuser", "exists@example.com", "password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.createUser(request));
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("testuser", response.username());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.getUserById(999L));
    }

    @Test
    void shouldGetUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserByUsername("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.username());
    }

    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(testUser));

        var responses = userService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals("testuser", responses.get(0).username());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UserRequest request = new UserRequest("updateduser", "updated@example.com", "newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateUser(1L, request);

        assertNotNull(response);
        assertEquals("updateduser", response.username());
        assertEquals("updated@example.com", response.email());
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.deleteUser(999L));
    }
}