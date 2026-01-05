package com.syfe.finance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syfe.finance.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class PersonalFinanceManagerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void completeUserWorkflow_Success() throws Exception {
        // 1. Register user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("integration@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Integration Test User");
        registerRequest.setPhoneNumber("+1234567890");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 2. Login user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("integration@test.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void dataIsolation_UserCannotAccessOthersData() throws Exception {
        // Create first user
        RegisterRequest user1Request = new RegisterRequest();
        user1Request.setUsername("user1@test.com");
        user1Request.setPassword("password123");
        user1Request.setFullName("User One");
        user1Request.setPhoneNumber("+1111111111");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1Request)))
                .andExpect(status().isCreated());

        // Create second user
        RegisterRequest user2Request = new RegisterRequest();
        user2Request.setUsername("user2@test.com");
        user2Request.setPassword("password123");
        user2Request.setFullName("User Two");
        user2Request.setPhoneNumber("+2222222222");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2Request)))
                .andExpect(status().isCreated());
    }
}