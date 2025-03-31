package com.example.shopappangular.responses;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Setter
@Getter
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
