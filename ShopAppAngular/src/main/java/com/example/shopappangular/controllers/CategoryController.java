package com.example.shopappangular.controllers;

import com.example.shopappangular.components.LocalizationUtils;
import com.example.shopappangular.dtos.CategoryDTO;
import com.example.shopappangular.models.Category;
import com.example.shopappangular.responses.CategoryResponse;
import com.example.shopappangular.responses.UpdateCategoryResponse;
import com.example.shopappangular.services.CategoryService;
import com.example.shopappangular.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@Validated có nghĩa là ở mức class ngoài nó dã ktra r ko thích hợp
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    //show all category
    @GetMapping("") //http://localhost:8019/api/v1/categories
    public ResponseEntity<List<Category>> getAllCategories(
                                                    @RequestParam("page") int page,
                                                    @RequestParam("limit") int limit
    ) {
        List<Category> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {
        CategoryResponse categoryResponse = new CategoryResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED));
            categoryResponse.setErrors(errorMessages);
           return ResponseEntity.badRequest().body(categoryResponse); //xuất ra lỗi can be empty
        }
        Category category = categoryService.createCategory(categoryDTO);
        categoryResponse.setCategory(category);
        return ResponseEntity.ok(categoryResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ){
        UpdateCategoryResponse updateCategoryResponse = new UpdateCategoryResponse();
        categoryService.updateCategory(id, categoryDTO);
        updateCategoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY));
        return ResponseEntity.ok(updateCategoryResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletedCategory(
            @PathVariable Long id
    ){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY));
    }

}
