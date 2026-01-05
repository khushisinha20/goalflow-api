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
public class StreakResponse {
    private Integer currentStreak;
    private Integer bestStreak;
    private LocalDate lastCompletedDate;
}
