package com.syfe.finance.service;

import com.syfe.finance.dto.TransactionRequest;
import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.Transaction;
import com.syfe.finance.entity.User;
import com.syfe.finance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;
    private Transaction transaction;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");

        category = new Category();
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
    void createTransaction_Success() {
        when(categoryService.findByNameAndUser("Salary", user)).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transactionRequest, user);

        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getAmount());
        assertEquals("Test transaction", result.getDescription());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_FutureDate_ThrowsException() {
        transactionRequest.setDate(LocalDate.now().plusDays(1));

        assertThrows(RuntimeException.class, () -> 
            transactionService.createTransaction(transactionRequest, user));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getUserTransactions_ReturnsUserTransactionsOnly() {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByUserOrderByDateDesc(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(user, null, null, null);

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    void updateTransaction_Success() {
        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAmount(new BigDecimal("6000.00"));
        updateRequest.setDescription("Updated transaction");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.updateTransaction(1L, updateRequest, user);

        assertNotNull(result);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_NotOwner_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(RuntimeException.class, () -> 
            transactionService.updateTransaction(1L, transactionRequest, otherUser));
    }

    @Test
    void deleteTransaction_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L, user);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_NotOwner_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(RuntimeException.class, () -> 
            transactionService.deleteTransaction(1L, otherUser));
    }
}