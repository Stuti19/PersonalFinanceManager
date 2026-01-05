package com.syfe.finance.service;

import com.syfe.finance.dto.CategoryRequest;
import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.User;
import com.syfe.finance.repository.CategoryRepository;
import com.syfe.finance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category defaultCategory;
    private Category customCategory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");

        defaultCategory = new Category();
        defaultCategory.setName("Salary");
        defaultCategory.setType(Category.CategoryType.INCOME);
        defaultCategory.setIsCustom(false);

        customCategory = new Category();
        customCategory.setName("CustomIncome");
        customCategory.setType(Category.CategoryType.INCOME);
        customCategory.setIsCustom(true);
        customCategory.setUser(user);
    }

    @Test
    void createCustomCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("CustomIncome");
        request.setType(Category.CategoryType.INCOME);

        when(categoryRepository.existsByNameAndUser("CustomIncome", user)).thenReturn(false);
        when(categoryRepository.existsByNameAndUserIsNull("CustomIncome")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(customCategory);

        Category result = categoryService.createCustomCategory(request, user);

        assertNotNull(result);
        assertEquals("CustomIncome", result.getName());
        assertTrue(result.getIsCustom());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCustomCategory_DuplicateName_ThrowsException() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Salary");
        request.setType(Category.CategoryType.INCOME);

        when(categoryRepository.existsByNameAndUserIsNull("Salary")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            categoryService.createCustomCategory(request, user));
    }

    @Test
    void getUserCategories_ReturnsDefaultAndCustom() {
        List<Category> defaultCategories = Arrays.asList(defaultCategory);
        List<Category> customCategories = Arrays.asList(customCategory);

        when(categoryRepository.findByUserIsNull()).thenReturn(defaultCategories);
        when(categoryRepository.findByUser(user)).thenReturn(customCategories);

        List<Category> result = categoryService.getUserCategories(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(defaultCategory));
        assertTrue(result.contains(customCategory));
    }

    @Test
    void deleteCustomCategory_Success() {
        when(categoryRepository.findByNameAndUser("CustomIncome", user)).thenReturn(customCategory);
        when(transactionRepository.existsByCategoryAndUser(customCategory, user)).thenReturn(false);

        categoryService.deleteCustomCategory("CustomIncome", user);

        verify(categoryRepository).delete(customCategory);
    }

    @Test
    void deleteCustomCategory_DefaultCategory_ThrowsException() {
        when(categoryRepository.findByNameAndUser("Salary", user)).thenReturn(null);
        when(categoryRepository.findByNameAndUserIsNull("Salary")).thenReturn(defaultCategory);

        assertThrows(RuntimeException.class, () -> 
            categoryService.deleteCustomCategory("Salary", user));
    }

    @Test
    void deleteCustomCategory_HasTransactions_ThrowsException() {
        when(categoryRepository.findByNameAndUser("CustomIncome", user)).thenReturn(customCategory);
        when(transactionRepository.existsByCategoryAndUser(customCategory, user)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            categoryService.deleteCustomCategory("CustomIncome", user));
    }
}