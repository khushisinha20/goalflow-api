package com.goalflow.goalflow_api.service;

import com.goalflow.goalflow_api.dto.request.LoginRequest;
import com.goalflow.goalflow_api.dto.request.SignupRequest;
import com.goalflow.goalflow_api.dto.response.JwtResponse;
import com.goalflow.goalflow_api.dto.response.MessageResponse;
import com.goalflow.goalflow_api.model.Streak;
import com.goalflow.goalflow_api.model.User;
import com.goalflow.goalflow_api.model.UserPreferences;
import com.goalflow.goalflow_api.repository.StreakRepository;
import com.goalflow.goalflow_api.repository.UserPreferencesRepository;
import com.goalflow.goalflow_api.repository.UserRepository;
import com.goalflow.goalflow_api.security.JwtUtils;
import com.goalflow.goalflow_api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StreakRepository streakRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getFullName()
        );
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFullName(signupRequest.getFullName());
        user.setTimezone(signupRequest.getTimezone());

        User savedUser = userRepository.save(user);

        // Create streak record
        Streak streak = new Streak();
        streak.setUser(savedUser);
        streak.setCurrentStreak(0);
        streak.setBestStreak(0);
        streakRepository.save(streak);

        // Create user preferences
        UserPreferences preferences = new UserPreferences();
        preferences.setUser(savedUser);
        userPreferencesRepository.save(preferences);

        return new MessageResponse("User registered successfully!");
    }
}
