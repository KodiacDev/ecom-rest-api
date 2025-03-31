package com.example.shopappangular.services.Impl;

import com.example.shopappangular.dtos.CategoryDTO;
import com.example.shopappangular.models.Category;
import com.example.shopappangular.repositories.CategoryRepository;
import com.example.shopappangular.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor //tự tạo hàm constructor ví dụ
//public ProductServiceImpl(CategoryRepository categoryRepository){
//        this.categoryRepository = categoryRepository;
//    }
public class CategorytServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Long id) { //findById() của JpaRepository trả về kiểu dữ liệu Optional<Category>
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        //Nếu trong Optional có giá trị (có Category tìm thấy) thì trả về giá trị đó
        //
        //Ngược lại, nếu trong Optional không có giá trị (không tìm thấy Category) thì ném ra ngoại lệ RuntimeException
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        //xóa xong
        categoryRepository.deleteById(id);
    }

}
