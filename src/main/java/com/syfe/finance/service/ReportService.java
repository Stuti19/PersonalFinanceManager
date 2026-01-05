package com.syfe.finance.service;

import com.syfe.finance.dto.MonthlyReportResponse;
import com.syfe.finance.dto.YearlyReportResponse;
import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.Transaction;
import com.syfe.finance.entity.User;
import com.syfe.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    public MonthlyReportResponse getMonthlyReport(int year, int month, User user) {
        List<Transaction> transactions = transactionRepository.findByUserAndYearAndMonth(user, year, month);
        
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        BigDecimal totalIncomeAmount = BigDecimal.ZERO;
        BigDecimal totalExpenseAmount = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Category.CategoryType.INCOME) {
                totalIncome.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
                totalIncomeAmount = totalIncomeAmount.add(transaction.getAmount());
            } else {
                totalExpenses.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
                totalExpenseAmount = totalExpenseAmount.add(transaction.getAmount());
            }
        }

        BigDecimal netSavings = totalIncomeAmount.subtract(totalExpenseAmount);

        return new MonthlyReportResponse(month, year, totalIncome, totalExpenses, netSavings);
    }

    public YearlyReportResponse getYearlyReport(int year, User user) {
        List<Transaction> transactions = transactionRepository.findByUserAndYear(user, year);
        
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        BigDecimal totalIncomeAmount = BigDecimal.ZERO;
        BigDecimal totalExpenseAmount = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Category.CategoryType.INCOME) {
                totalIncome.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
                totalIncomeAmount = totalIncomeAmount.add(transaction.getAmount());
            } else {
                totalExpenses.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
                totalExpenseAmount = totalExpenseAmount.add(transaction.getAmount());
            }
        }

        BigDecimal netSavings = totalIncomeAmount.subtract(totalExpenseAmount);

        return new YearlyReportResponse(year, totalIncome, totalExpenses, netSavings);
    }
}