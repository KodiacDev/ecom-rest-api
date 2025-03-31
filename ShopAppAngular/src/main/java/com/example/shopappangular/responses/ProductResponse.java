package com.example.shopappangular.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.shopappangular.models.Product;
import com.example.shopappangular.models.ProductImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse{
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse fromProduct(Product product){
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .productImages(product.getProductImages())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt()); //Trường createdAt và updatedAt là các thuộc tính kiểu date của Entity (Database). Nhưng trong ProductResponse là kiểu String để trả ra API
        //Do đó không thể trực tiếp map trường đó từ Entity sang Response được.
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
