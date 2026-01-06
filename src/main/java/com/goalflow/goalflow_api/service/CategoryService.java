package com.goalflow.goalflow_api.service;
import com.goalflow.goalflow_api.dto.request.CategoryRequest;
import com.goalflow.goalflow_api.dto.response.CategoryResponse;
import com.goalflow.goalflow_api.model.Category;
import com.goalflow.goalflow_api.model.User;
import com.goalflow.goalflow_api.repository.CategoryRepository;
import com.goalflow.goalflow_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CategoryResponse> getUserCategories(Long userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if category name already exists for user
        if (categoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category category = new Category();
        category.setUser(user);
        category.setName(request.getName());
        category.setColor(request.getColor());
        category.setIsDefault(false);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, Long userId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to category");
        }

        if (category.getIsDefault()) {
            throw new RuntimeException("Cannot modify default category");
        }

        // Check if new name conflicts with existing categories (excluding current)
        categoryRepository.findByUserIdAndName(userId, request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(categoryId)) {
                        throw new RuntimeException("Category with this name already exists");
                    }
                });

        category.setName(request.getName());
        category.setColor(request.getColor());

        Category updatedCategory = categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to category");
        }

        if (category.getIsDefault()) {
            throw new RuntimeException("Cannot delete default category");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .isDefault(category.getIsDefault())
                .createdAt(category.getCreatedAt())
                .build();
    }
}