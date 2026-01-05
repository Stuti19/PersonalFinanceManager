package com.syfe.finance.service;

import com.syfe.finance.dto.RegisterRequest;
import com.syfe.finance.entity.User;
import com.syfe.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("+1234567890");

        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setPassword("encodedPassword");
        user.setFullName("Test User");
        user.setPhoneNumber("+1234567890");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists_ThrowsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByUsername_UserExists() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getUsername());
    }

    @Test
    void findByUsername_UserNotExists() {
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent@example.com");

        assertFalse(result.isPresent());
    }
}