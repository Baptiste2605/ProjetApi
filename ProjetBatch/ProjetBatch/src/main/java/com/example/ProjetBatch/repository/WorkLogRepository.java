package com.example.ProjetBatch.repository;

import com.example.ProjetBatch.model.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    
    List<WorkLog> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate start, LocalDate end);

    @Query("""
        SELECT COALESCE(SUM(w.hours), 0)
        FROM WorkLog w
        WHERE w.employeeId = :empId
          AND w.workDate >= :start
          AND w.workDate < :end
    """)
    Double sumHoursForEmployeeAndMonth(
            @Param("empId") Long empId,
            @Param("start") LocalDate startInclusive,
            @Param("end") LocalDate endExclusive
    );
}