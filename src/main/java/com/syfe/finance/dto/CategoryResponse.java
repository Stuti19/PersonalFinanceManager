package com.syfe.finance.dto;

import com.syfe.finance.entity.Category;

public class CategoryResponse {
    private String name;
    private Category.CategoryType type;
    private boolean isCustom;

    public CategoryResponse() {}

    public CategoryResponse(String name, Category.CategoryType type, boolean isCustom) {
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category.CategoryType getType() { return type; }
    public void setType(Category.CategoryType type) { this.type = type; }

    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }

    // Getter for JSON serialization
    public boolean getIsCustom() { return isCustom; }
}