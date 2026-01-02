package com.goalflow.goalflow_api.controller;

import com.goalflow.goalflow_api.model.User;
import com.goalflow.goalflow_api.repository.UserRepository;
import com.goalflow.goalflow_api.repository.CategoryRepository;
import com.goalflow.goalflow_api.repository.StreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StreakRepository streakRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from GoalFlow API!";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend is running successfully!";
    }

    @GetMapping("/database")
    public Map<String, Object> testDatabase() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Count users
            long userCount = userRepository.count();
            response.put("userCount", userCount);

            // Get test user
            User testUser = userRepository.findByEmail("test@example.com").orElse(null);
            if (testUser != null) {
                response.put("testUser", Map.of(
                        "id", testUser.getId(),
                        "email", testUser.getEmail(),
                        "name", testUser.getFullName()
                ));

                // Count categories
                long categoryCount = categoryRepository.findByUserId(testUser.getId()).size();
                response.put("categoryCount", categoryCount);

                // Get streak
                streakRepository.findByUserId(testUser.getId()).ifPresent(streak -> {
                    response.put("streak", Map.of(
                            "current", streak.getCurrentStreak(),
                            "best", streak.getBestStreak()
                    ));
                });
            }

            response.put("status", "success");
            response.put("message", "Database connection successful!");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }
}
