package com.goalflow.goalflow_api.controller;
import com.goalflow.goalflow_api.dto.request.ExecutionRequest;
import com.goalflow.goalflow_api.dto.response.DailyStatsResponse;
import com.goalflow.goalflow_api.dto.response.ExecutionResponse;
import com.goalflow.goalflow_api.security.UserDetailsImpl;
import com.goalflow.goalflow_api.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/executions")
@CrossOrigin(origins = "http://localhost:4200")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @PostMapping("/{targetId}/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExecutionResponse> markAsCompleted(
            @PathVariable Long targetId,
            @RequestBody(required = false) ExecutionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (request == null) {
            request = new ExecutionRequest();
        }

        ExecutionResponse response = executionService.markAsCompleted(
                targetId,
                userDetails.getId(),
                request
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{targetId}/skip")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExecutionResponse> markAsSkipped(
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ExecutionResponse response = executionService.markAsSkipped(
                targetId,
                userDetails.getId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{targetId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExecutionResponse> getExecutionStatus(
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ExecutionResponse response = executionService.getExecutionStatus(
                targetId,
                userDetails.getId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily/{date}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyStatsResponse> getDailyStats(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        DailyStatsResponse response = executionService.getDailyStats(
                userDetails.getId(),
                date
        );

        return ResponseEntity.ok(response);
    }
}
