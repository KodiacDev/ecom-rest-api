package com.example.shopappangular.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Setter @Getter
@Data
@Builder
@Table(name = "categories")
public class Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

}
