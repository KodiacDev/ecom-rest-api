package com.example.shopappangular.services;

import com.example.shopappangular.dtos.ProductDTO;
import com.example.shopappangular.dtos.ProductImageDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.exceptions.InvalidParamException;
import com.example.shopappangular.models.Product;
import com.example.shopappangular.models.ProductImage;
import com.example.shopappangular.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(long id) throws Exception;

    List<Product> findProductByIds(List<Long> productIds);

    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);

    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException;


}
