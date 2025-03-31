package com.example.shopappangular.repositories;

import com.example.shopappangular.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);//phân trang

    @Query("SELECT p FROM Product p WHERE " + //nếu :categoryId IS NULL có nghĩa là không nhập thì lấy tất cả
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchProducts //java spring đã biến đoạn code này thành function
            (@Param("categoryId") Long categoryId,
             @Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId); //fetch là gọi 1 lần hết dữ liệu lun ko như lazy cần mới gọi thêm

    @Query("SELECT p FROM Product p WHERE p.id IN :productIds") // lấy nhiều sp thông qua nhiều id
    List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);


}
