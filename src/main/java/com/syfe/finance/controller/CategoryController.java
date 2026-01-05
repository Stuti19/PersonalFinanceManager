package com.syfe.finance.controller;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.User;
import com.syfe.finance.service.CategoryService;
import com.syfe.finance.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories(HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        CategoryListResponse response = categoryService.getAllCategories(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCustomCategory(@Valid @RequestBody CategoryRequest request,
                                                               HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        CategoryResponse response = categoryService.createCustomCategory(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> deleteCustomCategory(@PathVariable String name,
                                                                  HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser(httpRequest);
        categoryService.deleteCustomCategory(name, user);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
}