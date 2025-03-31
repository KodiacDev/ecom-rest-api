package com.example.shopappangular.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDateTime;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass //Mục đích chính là định nghĩa các trường chung có thể sử dụng lại cho nhiều entity.
public class BaseResponse {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
