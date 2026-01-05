package com.syfe.finance.controller;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.User;
import com.syfe.finance.service.TransactionService;
import com.syfe.finance.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request,
                                                               HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        TransactionResponse response = transactionService.createTransaction(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<TransactionListResponse> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category,
            HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        TransactionListResponse response = transactionService.getTransactions(user, startDate, endDate, category);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id,
                                                               @Valid @RequestBody TransactionUpdateRequest request,
                                                               HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        TransactionResponse response = transactionService.updateTransaction(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTransaction(@PathVariable Long id,
                                                               HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }
}