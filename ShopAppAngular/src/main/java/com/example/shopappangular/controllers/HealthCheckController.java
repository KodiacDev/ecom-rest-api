package com.example.shopappangular.controllers;
import com.example.shopappangular.models.Category;
import com.example.shopappangular.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("${api.prefix}/healthcheck")
@AllArgsConstructor
public class HealthCheckController {
    private final CategoryService categoryService;
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        // Perform additional health checks here
        try {
            List<Category> categories = categoryService.getAllCategory();
            // Get the computer name
            String computerName = InetAddress.getLocalHost().getHostName();
            return ResponseEntity.ok("ok, Computer Name: " + computerName);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("failed");
        }
    }
}
