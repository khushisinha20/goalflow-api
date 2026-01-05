package com.goalflow.goalflow_api.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStatsResponse {
    private LocalDate date;
    private Long totalTargets;
    private Long completedTargets;
    private Long skippedTargets;
    private Long missedTargets;
    private Long pendingTargets;
    private Double completionPercentage;
}