package com.example.ProjetApi.repository;

import com.example.ProjetApi.model.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
}