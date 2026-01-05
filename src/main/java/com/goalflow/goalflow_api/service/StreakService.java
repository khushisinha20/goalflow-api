package com.goalflow.goalflow_api.service;
import com.goalflow.goalflow_api.dto.response.StreakResponse;
import com.goalflow.goalflow_api.model.ExecutionStatus;
import com.goalflow.goalflow_api.model.Streak;
import com.goalflow.goalflow_api.model.Target;
import com.goalflow.goalflow_api.model.User;
import com.goalflow.goalflow_api.repository.StreakRepository;
import com.goalflow.goalflow_api.repository.TargetRepository;
import com.goalflow.goalflow_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StreakService {

    @Autowired
    private StreakRepository streakRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void updateStreakForDate(Long userId, LocalDate date) {
        Streak streak = streakRepository.findByUserId(userId)
                .orElseGet(() -> createNewStreak(userId));

        boolean allCompleted = checkAllTargetsCompleted(userId, date);

        if (allCompleted) {
            if (streak.getLastCompletedDate() != null) {
                long daysBetween = ChronoUnit.DAYS.between(
                        streak.getLastCompletedDate(), date
                );

                if (daysBetween == 1) {
                    streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                } else if (daysBetween > 1) {
                    streak.setCurrentStreak(1);
                } else if (daysBetween == 0) {

                }
            } else {
                streak.setCurrentStreak(1);
            }

            streak.setLastCompletedDate(date);

            if (streak.getCurrentStreak() > streak.getBestStreak()) {
                streak.setBestStreak(streak.getCurrentStreak());
            }
        } else {
            boolean hasMissed = checkHasMissedTargets(userId, date);

            if (hasMissed) {
                if (streak.getLastCompletedDate() != null) {
                    long daysBetween = ChronoUnit.DAYS.between(
                            streak.getLastCompletedDate(), date
                    );

                    if (daysBetween == 1) {
                        streak.setCurrentStreak(0);
                    }
                }
            }
        }

        streakRepository.save(streak);
    }

    public StreakResponse getStreakInfo(Long userId) {
        Streak streak = streakRepository.findByUserId(userId)
                .orElseGet(() -> createNewStreak(userId));

        return StreakResponse.builder()
                .currentStreak(streak.getCurrentStreak())
                .bestStreak(streak.getBestStreak())
                .lastCompletedDate(streak.getLastCompletedDate())
                .build();
    }

    private boolean checkAllTargetsCompleted(Long userId, LocalDate date) {
        List<Target> targets = targetRepository.findByUserIdAndTargetDate(userId, date);

        if (targets.isEmpty()) {
            return false;
        }

        return targets.stream()
                .allMatch(t -> t.getExecution() != null &&
                        t.getExecution().getStatus() == ExecutionStatus.COMPLETED);
    }

    private boolean checkHasMissedTargets(Long userId, LocalDate date) {
        List<Target> targets = targetRepository.findByUserIdAndTargetDate(userId, date);

        return targets.stream()
                .anyMatch(t -> t.getExecution() != null &&
                        t.getExecution().getStatus() == ExecutionStatus.MISSED);
    }

    private Streak createNewStreak(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Streak streak = new Streak();
        streak.setUser(user);
        streak.setCurrentStreak(0);
        streak.setBestStreak(0);

        return streakRepository.save(streak);
    }
}