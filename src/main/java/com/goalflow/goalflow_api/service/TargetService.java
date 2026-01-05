package com.goalflow.goalflow_api.service;
import com.goalflow.goalflow_api.dto.request.TargetRequest;
import com.goalflow.goalflow_api.dto.response.TargetResponse;
import com.goalflow.goalflow_api.model.Category;
import com.goalflow.goalflow_api.model.Execution;
import com.goalflow.goalflow_api.model.ExecutionStatus;
import com.goalflow.goalflow_api.model.Target;
import com.goalflow.goalflow_api.model.User;
import com.goalflow.goalflow_api.repository.CategoryRepository;
import com.goalflow.goalflow_api.repository.ExecutionRepository;
import com.goalflow.goalflow_api.repository.TargetRepository;
import com.goalflow.goalflow_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TargetService {

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public TargetResponse createTarget(Long userId, TargetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Verify category belongs to user
            if (!category.getUser().getId().equals(userId)) {
                throw new RuntimeException("Category does not belong to user");
            }
        }

        Target target = new Target();
        target.setUser(user);
        target.setTitle(request.getTitle());
        target.setDescription(request.getDescription());
        target.setTargetDate(request.getTargetDate());
        target.setCategory(category);

        Target savedTarget = targetRepository.save(target);

        // Create execution record
        Execution execution = new Execution();
        execution.setTarget(savedTarget);
        execution.setStatus(ExecutionStatus.PENDING);
        executionRepository.save(execution);

        return mapToResponse(savedTarget);
    }

    @Transactional
    public TargetResponse updateTarget(Long targetId, Long userId, TargetRequest request) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to target");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if (!category.getUser().getId().equals(userId)) {
                throw new RuntimeException("Category does not belong to user");
            }
        }

        target.setTitle(request.getTitle());
        target.setDescription(request.getDescription());
        target.setTargetDate(request.getTargetDate());
        target.setCategory(category);

        Target updatedTarget = targetRepository.save(target);

        return mapToResponse(updatedTarget);
    }

    @Transactional
    public void deleteTarget(Long targetId, Long userId) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to target");
        }

        targetRepository.delete(target);
    }

    public List<TargetResponse> getTargetsForDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Target> targets = targetRepository.findByUserIdAndTargetDateBetween(
                userId, startDate, endDate
        );

        return targets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TargetResponse getTargetById(Long targetId, Long userId) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to target");
        }

        return mapToResponse(target);
    }

    private TargetResponse mapToResponse(Target target) {
        TargetResponse.TargetResponseBuilder builder = TargetResponse.builder()
                .id(target.getId())
                .title(target.getTitle())
                .description(target.getDescription())
                .targetDate(target.getTargetDate())
                .createdAt(target.getCreatedAt())
                .updatedAt(target.getUpdatedAt());

        if (target.getCategory() != null) {
            builder.categoryId(target.getCategory().getId())
                    .categoryName(target.getCategory().getName())
                    .categoryColor(target.getCategory().getColor());
        }

        if (target.getExecution() != null) {
            builder.executionStatus(target.getExecution().getStatus())
                    .completionTime(target.getExecution().getCompletionTime());
        } else {
            builder.executionStatus(ExecutionStatus.PENDING);
        }

        return builder.build();
    }
}