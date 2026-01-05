package com.goalflow.goalflow_api.dto.response;
import com.goalflow.goalflow_api.model.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate targetDate;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private ExecutionStatus executionStatus;
    private LocalDateTime completionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}