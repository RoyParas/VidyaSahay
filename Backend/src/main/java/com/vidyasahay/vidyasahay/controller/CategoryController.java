package com.vidyasahay.vidyasahay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.response.CategoryResponse;
import com.vidyasahay.vidyasahay.repository.CategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> response = categoryRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(category -> category.getCode().toLowerCase()))
                .map(category -> new CategoryResponse(category.getId(), category.getCode()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
