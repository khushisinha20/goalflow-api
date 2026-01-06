package com.goalflow.goalflow_api.controller;
import com.goalflow.goalflow_api.dto.request.CategoryRequest;
import com.goalflow.goalflow_api.dto.response.CategoryResponse;
import com.goalflow.goalflow_api.dto.response.MessageResponse;
import com.goalflow.goalflow_api.security.UserDetailsImpl;
import com.goalflow.goalflow_api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CategoryResponse>> getUserCategories(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<CategoryResponse> categories = categoryService.getUserCategories(userDetails.getId());
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        CategoryResponse response = categoryService.createCategory(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        CategoryResponse response = categoryService.updateCategory(id, userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        categoryService.deleteCategory(id, userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Category deleted successfully"));
    }
}