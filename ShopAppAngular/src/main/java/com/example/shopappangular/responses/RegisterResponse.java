package com.example.shopappangular.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.shopappangular.models.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
