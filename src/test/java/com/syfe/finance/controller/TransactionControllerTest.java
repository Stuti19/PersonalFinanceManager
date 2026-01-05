package com.syfe.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syfe.finance.dto.TransactionRequest;
import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.Transaction;
import com.syfe.finance.entity.User;
import com.syfe.finance.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Transaction transaction;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");

        Category category = new Category();
        category.setName("Salary");
        category.setType(Category.CategoryType.INCOME);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("5000.00"));
        transaction.setDate(LocalDate.now());
        transaction.setCategory(category);
        transaction.setDescription("Test transaction");
        transaction.setUser(user);

        transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(new BigDecimal("5000.00"));
        transactionRequest.setDate(LocalDate.now());
        transactionRequest.setCategory("Salary");
        transactionRequest.setDescription("Test transaction");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequest.class), any(User.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.description").value("Test transaction"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getTransactions_Success() throws Exception {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getUserTransactions(any(User.class), any(), any(), any()))
                .thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].amount").value(5000.00));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateTransaction_Success() throws Exception {
        when(transactionService.updateTransaction(eq(1L), any(TransactionRequest.class), any(User.class)))
                .thenReturn(transaction);

        mockMvc.perform(put("/api/transactions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(5000.00));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteTransaction_Success() throws Exception {
        mockMvc.perform(delete("/api/transactions/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }

    @Test
    void createTransaction_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createTransaction_InvalidInput_BadRequest() throws Exception {
        transactionRequest.setAmount(null); // Invalid amount

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest());
    }
}