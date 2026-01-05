package com.goalflow.goalflow_api.controller;
import com.goalflow.goalflow_api.dto.request.TargetRequest;
import com.goalflow.goalflow_api.dto.response.MessageResponse;
import com.goalflow.goalflow_api.dto.response.TargetResponse;
import com.goalflow.goalflow_api.security.UserDetailsImpl;
import com.goalflow.goalflow_api.service.TargetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/targets")
@CrossOrigin(origins = "http://localhost:4200")
public class TargetController {

    @Autowired
    private TargetService targetService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TargetResponse> createTarget(
            @Valid @RequestBody TargetRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        TargetResponse response = targetService.createTarget(
                userDetails.getId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TargetResponse>> getTargets(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<TargetResponse> targets = targetService.getTargetsForDateRange(
                userDetails.getId(),
                startDate,
                endDate
        );
        return ResponseEntity.ok(targets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TargetResponse> getTargetById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        TargetResponse response = targetService.getTargetById(id, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TargetResponse> updateTarget(
            @PathVariable Long id,
            @Valid @RequestBody TargetRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        TargetResponse response = targetService.updateTarget(
                id,
                userDetails.getId(),
                request
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteTarget(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        targetService.deleteTarget(id, userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Target deleted successfully"));
    }
}