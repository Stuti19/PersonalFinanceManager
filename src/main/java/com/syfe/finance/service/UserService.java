package com.syfe.finance.service;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.User;
import com.syfe.finance.exception.DuplicateResourceException;
import com.syfe.finance.exception.UnauthorizedException;
import com.syfe.finance.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository = 
        new HttpSessionSecurityContextRepository();

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
     * @param httpResponse the HTTP response to set session cookie
     * @return AuthResponse with success message
     * @throws UnauthorizedException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            // Authenticate user using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            // Create security context and store in session
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Save security context to session (this will set the JSESSIONID cookie)
            securityContextRepository.saveContext(securityContext, httpRequest, httpResponse);

            // Also store userId in session for backward compatibility
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            return new AuthResponse("Login successful");
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    public AuthResponse logout(HttpServletRequest request, HttpServletResponse httpResponse) {
        // Clear Spring Security context
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);
        // Save empty context to clear it from session
        securityContextRepository.saveContext(securityContext, request, httpResponse);
        
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new AuthResponse("Logout successful");
    }

    public User getCurrentUser(HttpServletRequest request) {
        // Only use Spring Security authentication - no fallback to ensure proper validation
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No active session");
        }
        
        // Verify the authentication principal is valid
        if (!(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails)) {
            throw new UnauthorizedException("Invalid authentication");
        }
        
        String username = authentication.getName();
        if (username == null || username.isEmpty()) {
            throw new UnauthorizedException("Invalid authentication");
        }
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}