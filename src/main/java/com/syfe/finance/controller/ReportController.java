package com.syfe.finance.controller;

import com.syfe.finance.dto.MonthlyReportResponse;
import com.syfe.finance.dto.YearlyReportResponse;
import com.syfe.finance.entity.User;
import com.syfe.finance.service.ReportService;
import com.syfe.finance.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(@PathVariable int year,
                                                                @PathVariable int month,
                                                                HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        MonthlyReportResponse response = reportService.getMonthlyReport(year, month, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(@PathVariable int year,
                                                              HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        YearlyReportResponse response = reportService.getYearlyReport(year, user);
        return ResponseEntity.ok(response);
    }
}