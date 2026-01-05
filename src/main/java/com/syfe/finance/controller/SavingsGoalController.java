package com.syfe.finance.controller;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.User;
import com.syfe.finance.service.SavingsGoalService;
import com.syfe.finance.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@Valid @RequestBody SavingsGoalRequest request,
                                                        HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        SavingsGoalResponse response = savingsGoalService.createGoal(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<SavingsGoalListResponse> getAllGoals(HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        SavingsGoalListResponse response = savingsGoalService.getAllGoals(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getGoal(@PathVariable Long id,
                                                     HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        SavingsGoalResponse response = savingsGoalService.getGoal(id, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(@PathVariable Long id,
                                                        @Valid @RequestBody SavingsGoalUpdateRequest request,
                                                        HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        SavingsGoalResponse response = savingsGoalService.updateGoal(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(@PathVariable Long id,
                                                        HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        savingsGoalService.deleteGoal(id, user);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }
}