package com.goalflow.goalflow_api.repository;

import com.goalflow.goalflow_api.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {

    Optional<Streak> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Streak s SET s.currentStreak = :streak, " +
            "s.lastCompletedDate = :date WHERE s.user.id = :userId")
    void updateStreak(
            @Param("userId") Long userId,
            @Param("streak") Integer streak,
            @Param("date") LocalDate date
    );
}