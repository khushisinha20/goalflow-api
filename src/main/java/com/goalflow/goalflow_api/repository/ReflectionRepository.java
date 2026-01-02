package com.goalflow.goalflow_api.repository;

import com.goalflow.goalflow_api.model.Reflection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

    List<Reflection> findByUserIdOrderByYearDescMonthDesc(Long userId);

    Optional<Reflection> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
}