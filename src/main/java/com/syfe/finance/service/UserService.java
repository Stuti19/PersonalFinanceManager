package com.syfe.finance.service;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.User;
import com.syfe.finance.exception.DuplicateResourceException;
import com.syfe.finance.exception.UnauthorizedException;
import com.syfe.finance.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user authentication and registration.
 * Handles user creation, validation, and authentication operations.
 * 
 * @author Personal Finance Manager
 * @version 1.0.0
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     * 
     * @param request the registration request containing user details
     * @return AuthResponse with success message and user ID
     * @throws DuplicateResourceException if username already exists
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getFullName(),
            request.getPhoneNumber()
        );

        User savedUser = userRepository.save(user);
        return new AuthResponse("User registered successfully", savedUser.getId());
    }

    /**
     * Authenticates a user and creates a session.
     * 
     * @param request the login request with username and password
     * @param httpRequest the HTTP request to create session
     * @return AuthResponse with success message
     * @throws UnauthorizedException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Create session
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        return new AuthResponse("Login successful");
    }

    public AuthResponse logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new AuthResponse("Logout successful");
    }

    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException("No active session");
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("No active session");
        }

        return userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}