package com.goalflow.goalflow_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from GoalFlow API!";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend is running successfully!";
    }
}
