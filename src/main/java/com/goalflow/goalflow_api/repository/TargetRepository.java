package com.goalflow.goalflow_api.repository;

import com.goalflow.goalflow_api.model.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {

    List<Target> findByUserIdAndTargetDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Target> findByUserIdAndTargetDate(Long userId, LocalDate date);

    @Query("SELECT t FROM Target t WHERE t.user.id = :userId " +
            "AND t.targetDate = :date AND t.execution.status = 'PENDING'")
    List<Target> findPendingTargetsForDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("SELECT COUNT(t) FROM Target t WHERE t.user.id = :userId " +
            "AND t.targetDate BETWEEN :startDate AND :endDate")
    Long countTargetsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(t) FROM Target t " +
            "WHERE t.user.id = :userId " +
            "AND t.targetDate BETWEEN :startDate AND :endDate " +
            "AND t.execution.status = 'COMPLETED'")
    Long countCompletedTargetsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}