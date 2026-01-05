package com.syfe.finance.config;

import com.syfe.finance.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new SessionAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                })
            )
            .headers(headers -> headers.frameOptions().sameOrigin());
        
        return http.build();
    }
    
    // Custom filter to check session-based authentication
    private static class SessionAuthenticationFilter extends OncePerRequestFilter {
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, 
                                       jakarta.servlet.http.HttpServletResponse response, 
                                       jakarta.servlet.FilterChain filterChain) 
                throws jakarta.servlet.ServletException, java.io.IOException {
            
            HttpSession session = request.getSession(false);
            
            if (session != null && session.getAttribute("userId") != null) {
                Long userId = (Long) session.getAttribute("userId");
                
                // Create an authenticated token
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, java.util.Collections.emptyList());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
            filterChain.doFilter(request, response);
        }
    }
}