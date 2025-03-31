package com.example.shopappangular.repositories;

import com.example.shopappangular.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //chỗ @Repository có thể bỏ vì java biết khi ta extends Jpa đã biết là repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
