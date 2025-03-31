package com.example.shopappangular.services;

import com.example.shopappangular.dtos.CategoryDTO;
import com.example.shopappangular.models.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category getCategoryById(Long id);

    List<Category> getAllCategory();

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}
