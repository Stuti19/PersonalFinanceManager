package com.syfe.finance.service;

import com.syfe.finance.dto.SavingsGoalRequest;
import com.syfe.finance.entity.SavingsGoal;
import com.syfe.finance.entity.User;
import com.syfe.finance.repository.SavingsGoalRepository;
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
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private User user;
    private SavingsGoal savingsGoal;
    private SavingsGoalRequest goalRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");

        savingsGoal = new SavingsGoal();
        savingsGoal.setId(1L);
        savingsGoal.setGoalName("Emergency Fund");
        savingsGoal.setTargetAmount(new BigDecimal("5000.00"));
        savingsGoal.setTargetDate(LocalDate.now().plusMonths(12));
        savingsGoal.setStartDate(LocalDate.now());
        savingsGoal.setUser(user);

        goalRequest = new SavingsGoalRequest();
        goalRequest.setGoalName("Emergency Fund");
        goalRequest.setTargetAmount(new BigDecimal("5000.00"));
        goalRequest.setTargetDate(LocalDate.now().plusMonths(12));
        goalRequest.setStartDate(LocalDate.now());
    }

    @Test
    void createGoal_Success() {
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(savingsGoal);

        SavingsGoal result = savingsGoalService.createGoal(goalRequest, user);

        assertNotNull(result);
        assertEquals("Emergency Fund", result.getGoalName());
        assertEquals(new BigDecimal("5000.00"), result.getTargetAmount());
        verify(savingsGoalRepository).save(any(SavingsGoal.class));
    }

    @Test
    void createGoal_PastTargetDate_ThrowsException() {
        goalRequest.setTargetDate(LocalDate.now().minusDays(1));

        assertThrows(RuntimeException.class, () -> 
            savingsGoalService.createGoal(goalRequest, user));
    }

    @Test
    void getUserGoals_ReturnsUserGoalsOnly() {
        List<SavingsGoal> goals = Arrays.asList(savingsGoal);
        when(savingsGoalRepository.findByUser(user)).thenReturn(goals);

        List<SavingsGoal> result = savingsGoalService.getUserGoals(user);

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    void getGoalById_Success() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(savingsGoal));

        SavingsGoal result = savingsGoalService.getGoalById(1L, user);

        assertNotNull(result);
        assertEquals("Emergency Fund", result.getGoalName());
    }

    @Test
    void getGoalById_NotOwner_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);

        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(savingsGoal));

        assertThrows(RuntimeException.class, () -> 
            savingsGoalService.getGoalById(1L, otherUser));
    }

    @Test
    void updateGoal_Success() {
        SavingsGoalRequest updateRequest = new SavingsGoalRequest();
        updateRequest.setTargetAmount(new BigDecimal("6000.00"));
        updateRequest.setTargetDate(LocalDate.now().plusMonths(15));

        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(savingsGoal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(savingsGoal);

        SavingsGoal result = savingsGoalService.updateGoal(1L, updateRequest, user);

        assertNotNull(result);
        verify(savingsGoalRepository).save(any(SavingsGoal.class));
    }

    @Test
    void deleteGoal_Success() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(savingsGoal));

        savingsGoalService.deleteGoal(1L, user);

        verify(savingsGoalRepository).delete(savingsGoal);
    }

    @Test
    void deleteGoal_NotOwner_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);

        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(savingsGoal));

        assertThrows(RuntimeException.class, () -> 
            savingsGoalService.deleteGoal(1L, otherUser));
    }
}