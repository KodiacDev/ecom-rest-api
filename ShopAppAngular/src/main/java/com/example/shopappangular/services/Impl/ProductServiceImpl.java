package com.example.shopappangular.services.Impl;

import com.example.shopappangular.dtos.ProductDTO;
import com.example.shopappangular.dtos.ProductImageDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.exceptions.InvalidParamException;
import com.example.shopappangular.models.Category;
import com.example.shopappangular.models.Product;
import com.example.shopappangular.models.ProductImage;
import com.example.shopappangular.repositories.CategoryRepository;
import com.example.shopappangular.repositories.ProductImageRepository;
import com.example.shopappangular.repositories.ProductRepository;
import com.example.shopappangular.responses.ProductResponse;
import com.example.shopappangular.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    @Transactional
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: "+productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if(optionalProduct.isPresent()) {
            return optionalProduct.get();
        } //optionalProduct.isPresent() trả về true nếu chứa giá trị Product bên trong, false nếu rỗng.
        throw new DataNotFoundException("Cannot find product with id =" + productId);
    }

    @Override
    public List<Product> findProductByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId, PageRequest pageRequest) {
        //Lấy ds sp theo trang(page) và gioi hạn(limit), và categoryId nếu có
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productPage.map(ProductResponse::fromProduct); //ProductResponse::fromProduct có nghĩa là truy cập phương thức như .from(product) //là nhiều sản phẩm nên ta .map để lặp
    }
    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if(existingProduct != null) {
            //copy các thuộc tính từ DTO -> Product
            //Có thể sử dụng ModelMapper
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() ->
                            new DataNotFoundException(
                                    "Cannot find category with id: "+productDTO.getCategoryId()));
            if(productDTO.getName() != null && !productDTO.getName().isEmpty()) {
                existingProduct.setName(productDTO.getName());
            }

            existingProduct.setCategory(existingCategory);
            if(productDTO.getPrice() >= 0) {
                existingProduct.setPrice(productDTO.getPrice());
            }
            if(productDTO.getDescription() != null &&
                    !productDTO.getDescription().isEmpty()) {
                existingProduct.setDescription(productDTO.getDescription());
            }
            if(productDTO.getThumbnail() != null &&
                    !productDTO.getThumbnail().isEmpty()) {
                existingProduct.setDescription(productDTO.getThumbnail());
            }
            return productRepository.save(existingProduct);
        }
        return null;

    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete); //ProductRepository là interface kế thừa JpaRepository có sẵn phương thức delete(Product product):: là toán tử tham chiếu đến phương thức (method reference operator)
        //ifPresent dưới dạng lambda expression hoặc method reference. Nếu không có giá trị (object null) thì không thực hiện phần tử đó.
    }



    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws InvalidParamException, DataNotFoundException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find product with id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= 5) {
            throw new InvalidParamException("Number of images must be <= 5");
        }
        return productImageRepository.save(newProductImage);
    }
}
