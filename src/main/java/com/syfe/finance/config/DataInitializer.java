package com.syfe.finance.config;

import com.syfe.finance.entity.Category;
import com.syfe.finance.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        // Income categories
        createDefaultCategoryIfNotExists("Salary", Category.CategoryType.INCOME);

        // Expense categories
        createDefaultCategoryIfNotExists("Food", Category.CategoryType.EXPENSE);
        createDefaultCategoryIfNotExists("Rent", Category.CategoryType.EXPENSE);
        createDefaultCategoryIfNotExists("Transportation", Category.CategoryType.EXPENSE);
        createDefaultCategoryIfNotExists("Entertainment", Category.CategoryType.EXPENSE);
        createDefaultCategoryIfNotExists("Healthcare", Category.CategoryType.EXPENSE);
        createDefaultCategoryIfNotExists("Utilities", Category.CategoryType.EXPENSE);
    }

    private void createDefaultCategoryIfNotExists(String name, Category.CategoryType type) {
        if (!categoryRepository.existsByNameAndUserIsNull(name)) {
            Category category = new Category();
            category.setName(name);
            category.setType(type);
            category.setIsCustom(false);
            category.setUser(null); // Default categories have no user
            categoryRepository.save(category);
        }
    }
}