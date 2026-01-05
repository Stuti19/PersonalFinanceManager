package com.syfe.finance.service;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.Transaction;
import com.syfe.finance.entity.User;
import com.syfe.finance.exception.BadRequestException;
import com.syfe.finance.exception.ForbiddenException;
import com.syfe.finance.exception.ResourceNotFoundException;
import com.syfe.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryService categoryService;

    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        if (!categoryService.isValidCategory(request.getCategory(), user)) {
            throw new BadRequestException("Invalid category");
        }

        Category.CategoryType categoryType = categoryService.getCategoryType(request.getCategory(), user);
        
        Transaction transaction = new Transaction(
            request.getAmount(),
            request.getDate(),
            request.getCategory(),
            request.getDescription(),
            categoryType,
            user
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }

    public TransactionListResponse getTransactions(User user, LocalDate startDate, LocalDate endDate, String category) {
        List<Transaction> transactions;
        
        if (startDate != null || endDate != null || category != null) {
            if (category != null && !categoryService.isValidCategory(category, user)) {
                throw new BadRequestException("Invalid category");
            }
            transactions = transactionRepository.findByUserWithFilters(user, startDate, endDate, category);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDesc(user);
        }

        List<TransactionResponse> transactionResponses = transactions.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());

        return new TransactionListResponse(transactionResponses);
    }

    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User user) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot update transaction that doesn't belong to you");
        }

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getCategory() != null) {
            if (!categoryService.isValidCategory(request.getCategory(), user)) {
                throw new BadRequestException("Invalid category");
            }
            transaction.setCategory(request.getCategory());
            transaction.setType(categoryService.getCategoryType(request.getCategory(), user));
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }

    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot delete transaction that doesn't belong to you");
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getDate(),
            transaction.getCategory(),
            transaction.getDescription(),
            transaction.getType()
        );
    }
}