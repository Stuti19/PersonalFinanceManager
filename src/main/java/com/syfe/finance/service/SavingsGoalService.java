package com.syfe.finance.service;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.SavingsGoal;
import com.syfe.finance.entity.User;
import com.syfe.finance.exception.ForbiddenException;
import com.syfe.finance.exception.ResourceNotFoundException;
import com.syfe.finance.repository.SavingsGoalRepository;
import com.syfe.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public SavingsGoalResponse createGoal(SavingsGoalRequest request, User user) {
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        
        SavingsGoal goal = new SavingsGoal(
            request.getGoalName(),
            request.getTargetAmount(),
            request.getTargetDate(),
            startDate,
            user
        );

        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        return convertToResponse(savedGoal, user);
    }

    public SavingsGoalListResponse getAllGoals(User user) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUser(user);
        List<SavingsGoalResponse> goalResponses = goals.stream()
            .map(goal -> convertToResponse(goal, user))
            .collect(Collectors.toList());
        return new SavingsGoalListResponse(goalResponses);
    }

    public SavingsGoalResponse getGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot access goal that doesn't belong to you");
        }

        return convertToResponse(goal, user);
    }

    public SavingsGoalResponse updateGoal(Long id, SavingsGoalUpdateRequest request, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot update goal that doesn't belong to you");
        }

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return convertToResponse(updatedGoal, user);
    }

    public void deleteGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot delete goal that doesn't belong to you");
        }

        savingsGoalRepository.delete(goal);
    }

    private SavingsGoalResponse convertToResponse(SavingsGoal goal, User user) {
        BigDecimal currentProgress = transactionRepository.calculateNetSavingsSince(user, goal.getStartDate());
        if (currentProgress == null) {
            currentProgress = BigDecimal.ZERO;
        }

        double progressPercentage = currentProgress.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();

        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }

        return new SavingsGoalResponse(
            goal.getId(),
            goal.getGoalName(),
            goal.getTargetAmount(),
            goal.getTargetDate(),
            goal.getStartDate(),
            currentProgress,
            Math.round(progressPercentage * 100.0) / 100.0,
            remainingAmount
        );
    }
}