package com.goalflow.goalflow_api.repository;

import com.goalflow.goalflow_api.model.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {

    Optional<Execution> findByTargetId(Long targetId);

    @Query("SELECT e FROM Execution e " +
            "WHERE e.target.user.id = :userId " +
            "AND e.target.targetDate = :date")
    List<Execution> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Modifying
    @Query("UPDATE Execution e SET e.status = 'MISSED' " +
            "WHERE e.status = 'PENDING' " +
            "AND e.target.targetDate < :currentDate")
    int markPendingAsMissed(@Param("currentDate") LocalDate currentDate);
}