package com.example.shopappangular.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.logging.log4j.message.Message;

@Data
@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category's name cannot be empty")
    private String name;
}
