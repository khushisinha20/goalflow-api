package com.goalflow.goalflow_api.dto.response;

import com.goalflow.goalflow_api.model.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResponse {
    private Long id;
    private Long targetId;
    private ExecutionStatus status;
    private LocalDateTime completionTime;
    private String notes;
    private LocalDateTime createdAt;
}