package com.example.ProjetApi.repository;

import com.example.ProjetApi.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
   
    List<LeaveRequest> findByEmployee_Id(Long id);
    void deleteByEmployee_Id(Long id);
}