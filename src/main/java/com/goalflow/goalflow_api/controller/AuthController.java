package com.goalflow.goalflow_api.controller;
import com.goalflow.goalflow_api.dto.request.LoginRequest;
import com.goalflow.goalflow_api.dto.request.SignupRequest;
import com.goalflow.goalflow_api.dto.response.JwtResponse;
import com.goalflow.goalflow_api.dto.response.MessageResponse;
import com.goalflow.goalflow_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid email or password!"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            MessageResponse response = authService.registerUser(signupRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Could not register user!"));
        }
    }
}