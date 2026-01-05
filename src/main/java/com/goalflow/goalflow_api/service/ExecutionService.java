package com.goalflow.goalflow_api.service;
import com.goalflow.goalflow_api.dto.request.ExecutionRequest;
import com.goalflow.goalflow_api.dto.response.DailyStatsResponse;
import com.goalflow.goalflow_api.dto.response.ExecutionResponse;
import com.goalflow.goalflow_api.model.Execution;
import com.goalflow.goalflow_api.model.ExecutionStatus;
import com.goalflow.goalflow_api.model.Target;
import com.goalflow.goalflow_api.repository.ExecutionRepository;
import com.goalflow.goalflow_api.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutionService {

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private StreakService streakService;

    @Transactional
    public ExecutionResponse markAsCompleted(Long targetId, Long userId, ExecutionRequest request) {
        // Get target and verify ownership
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to target");
        }

        // Get or create execution
        Execution execution = executionRepository.findByTargetId(targetId)
                .orElseGet(() -> {
                    Execution newExecution = new Execution();
                    newExecution.setTarget(target);
                    newExecution.setStatus(ExecutionStatus.PENDING);
                    return newExecution;
                });

        // Update execution
        execution.setStatus(ExecutionStatus.COMPLETED);
        execution.setCompletionTime(LocalDateTime.now());
        execution.setNotes(request.getNotes());

        Execution savedExecution = executionRepository.save(execution);

        // Update streak
        streakService.updateStreakForDate(userId, target.getTargetDate());

        return mapToResponse(savedExecution);
    }

    @Transactional
    public ExecutionResponse markAsSkipped(Long targetId, Long userId) {
        // Get target and verify ownership
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to target");
        }

        // Get or create execution
        Execution execution = executionRepository.findByTargetId(targetId)
                .orElseGet(() -> {
                    Execution newExecution = new Execution();
                    newExecution.setTarget(target);
                    newExecution.setStatus(ExecutionStatus.PENDING);
                    return newExecution;
                });

        // Update execution
        execution.setStatus(ExecutionStatus.SKIPPED);
        execution.setCompletionTime(LocalDateTime.now());

        Execution savedExecution = executionRepository.save(execution);

        // Skipped targets don't break streak, but recalculate anyway
        streakService.updateStreakForDate(userId, target.getTargetDate());

        return mapToResponse(savedExecution);
    }

    public ExecutionResponse getExecutionStatus(Long targetId, Long userId) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to target");
        }

        Execution execution = executionRepository.findByTargetId(targetId)
                .orElseGet(() -> {
                    Execution newExecution = new Execution();
                    newExecution.setTarget(target);
                    newExecution.setStatus(ExecutionStatus.PENDING);
                    return newExecution;
                });

        return mapToResponse(execution);
    }

    public DailyStatsResponse getDailyStats(Long userId, LocalDate date) {
        List<Target> targets = targetRepository.findByUserIdAndTargetDate(userId, date);

        long total = targets.size();
        long completed = targets.stream()
                .filter(t -> t.getExecution() != null && t.getExecution().getStatus() == ExecutionStatus.COMPLETED)
                .count();
        long skipped = targets.stream()
                .filter(t -> t.getExecution() != null && t.getExecution().getStatus() == ExecutionStatus.SKIPPED)
                .count();
        long missed = targets.stream()
                .filter(t -> t.getExecution() != null && t.getExecution().getStatus() == ExecutionStatus.MISSED)
                .count();
        long pending = targets.stream()
                .filter(t -> t.getExecution() == null || t.getExecution().getStatus() == ExecutionStatus.PENDING)
                .count();

        double completionPercentage = total > 0 ? (completed * 100.0 / total) : 0;

        return DailyStatsResponse.builder()
                .date(date)
                .totalTargets(total)
                .completedTargets(completed)
                .skippedTargets(skipped)
                .missedTargets(missed)
                .pendingTargets(pending)
                .completionPercentage(completionPercentage)
                .build();
    }

    private ExecutionResponse mapToResponse(Execution execution) {
        return ExecutionResponse.builder()
                .id(execution.getId())
                .targetId(execution.getTarget().getId())
                .status(execution.getStatus())
                .completionTime(execution.getCompletionTime())
                .notes(execution.getNotes())
                .createdAt(execution.getCreatedAt())
                .build();
    }
}