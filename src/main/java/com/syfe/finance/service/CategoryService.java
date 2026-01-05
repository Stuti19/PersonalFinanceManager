package com.syfe.finance.service;

import com.syfe.finance.dto.*;
import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.User;
import com.syfe.finance.exception.BadRequestException;
import com.syfe.finance.exception.DuplicateResourceException;
import com.syfe.finance.exception.ForbiddenException;
import com.syfe.finance.exception.ResourceNotFoundException;
import com.syfe.finance.repository.CategoryRepository;
import com.syfe.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public CategoryListResponse getAllCategories(User user) {
        List<Category> categories = categoryRepository.findAllAvailableForUser(user);
        List<CategoryResponse> categoryResponses = categories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return new CategoryListResponse(categoryResponses);
    }

    public CategoryResponse createCustomCategory(CategoryRequest request, User user) {
        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new DuplicateResourceException("Category name already exists");
        }

        // Check if default category with same name exists
        if (categoryRepository.findByNameAndUser(request.getName(), null).isPresent()) {
            throw new DuplicateResourceException("Category name conflicts with default category");
        }

        Category category = new Category(request.getName(), request.getType(), true, user);
        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    public void deleteCustomCategory(String name, User user) {
        Category category = categoryRepository.findByNameAndUser(name, user)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.isCustom()) {
            throw new ForbiddenException("Cannot delete default category");
        }

        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot delete category that doesn't belong to you");
        }

        // Check if category is referenced by any transactions
        boolean hasTransactions = transactionRepository.findByUserOrderByDateDesc(user)
            .stream()
            .anyMatch(t -> t.getCategory().equals(name));

        if (hasTransactions) {
            throw new BadRequestException("Cannot delete category that is referenced by transactions");
        }

        categoryRepository.delete(category);
    }

    public boolean isValidCategory(String categoryName, User user) {
        return categoryRepository.findByNameAndUser(categoryName, user).isPresent();
    }

    public Category.CategoryType getCategoryType(String categoryName, User user) {
        return categoryRepository.findByNameAndUser(categoryName, user)
            .map(Category::getType)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(category.getName(), category.getType(), category.isCustom());
    }
}