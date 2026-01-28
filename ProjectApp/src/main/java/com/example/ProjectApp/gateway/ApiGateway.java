package com.example.ProjectApp.gateway;

import com.example.ProjectApp.model.EmployeeDto;
import com.example.ProjectApp.model.LeaveRequestDto;
import com.example.ProjectApp.model.PayslipDto;
import com.example.ProjectApp.model.PosteDto;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.time.LocalDate;

public interface ApiGateway {

    List<EmployeeDto> listEmployees();
    EmployeeDto createEmployee(EmployeeDto e);
    void deleteEmployee(Long id);
    List<PayslipDto> listPayslipsByEmployee(Long employeeId);
    EmployeeDto updateEmployee(Long id, EmployeeDto e);
    List<PosteDto> listPoste();
    ResponseEntity<byte[]> downloadPayslip(Long payslipId);
    EmployeeDto login(String email, String password);
    void upsertHours(Long employeeId, LocalDate month, Double week1, Double week2, Double week3, Double week4);
    List<LeaveRequestDto> listLeaves(Long employeeId);
    List<LeaveRequestDto> listAllLeaves();
    void updateLeaveStatus(Long id, String status);
    LeaveRequestDto createLeave(Long employeeId, LeaveRequestDto dto);
    EmployeeDto getEmployee(Long id);
}