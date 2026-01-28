package com.example.ProjetBatch.repository;

import com.example.ProjetBatch.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    // Permet de trouver les congés d'un employé
    List<LeaveRequest> findByEmployee_Id(Long id);
}