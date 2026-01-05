package com.goalflow.goalflow_api.controller;

import com.goalflow.goalflow_api.dto.response.StreakResponse;
import com.goalflow.goalflow_api.security.UserDetailsImpl;
import com.goalflow.goalflow_api.service.StreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/streaks")
@CrossOrigin(origins = "http://localhost:4200")
public class StreakController {

    @Autowired
    private StreakService streakService;

    @GetMapping("/current")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StreakResponse> getCurrentStreak(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        StreakResponse response = streakService.getStreakInfo(userDetails.getId());
        return ResponseEntity.ok(response);
    }
}