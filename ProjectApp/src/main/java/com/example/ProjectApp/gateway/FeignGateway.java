package com.example.ProjectApp.gateway;

import com.example.ProjectApp.model.EmployeeDto;
import com.example.ProjectApp.model.LeaveRequestDto;
import com.example.ProjectApp.model.PayslipDto;
import com.example.ProjectApp.model.PosteDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@FeignClient(name = "employeeApi", url = "${app.api.url}")
interface EmployeeApiClient {

    // --- Employees ---
    @GetMapping("/api/employees")
    List<EmployeeDto> listEmployees();

    @PostMapping("/api/employees")
    EmployeeDto createEmployee(@RequestBody EmployeeDto e);

    @PutMapping("/api/employees/{id}")
    EmployeeDto updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeDto e);

    @DeleteMapping("/api/employees/{id}")
    void deleteEmployee(@PathVariable("id") Long id);

    // --- Payslips ---
    @GetMapping("/api/payslips/by-employee/{employeeId}")
    List<PayslipDto> listPayslipsByEmployee(@PathVariable("employeeId") Long employeeId);


    @GetMapping("/api/postes")
    List<PosteDto> listPostes();
    @GetMapping(value = "/api/payslips/{id}/download")
    ResponseEntity<byte[]> downloadPayslip(@PathVariable("id") Long payslipId);
    
    // NEW: login via Map (pas de DTO)
    @PostMapping("/api/auth/login")
    EmployeeDto login(@RequestBody Map<String, String> body);
    
    @PostMapping("/api/hours")
    void upsertHours(@RequestParam("employeeId") Long employeeId,
                     @RequestParam("month") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
                     @RequestParam(value = "week1", required = false) Double week1,
                     @RequestParam(value = "week2", required = false) Double week2,
                     @RequestParam(value = "week3", required = false) Double week3,
                     @RequestParam(value = "week4", required = false) Double week4);

    @GetMapping("/api/leaves")
    List<LeaveRequestDto> getAllLeaves();

    // Valider/Refuser une demande
    @PutMapping("/api/leaves/{id}")
    LeaveRequestDto updateLeaveStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
    
    @GetMapping("/api/leaves/employee/{empId}")
    List<LeaveRequestDto> listLeavesByEmployee(@PathVariable("empId") Long empId);

    @PostMapping("/api/leaves/employee/{empId}")
    LeaveRequestDto createLeave(@PathVariable("empId") Long empId, @RequestBody LeaveRequestDto dto);

    @GetMapping("/api/employees/{id}")
    EmployeeDto getEmployee(@PathVariable("id") Long id);

}

/* =======================
   FEIGN GATEWAY (BEAN)
   ======================= */
@Component
@ConditionalOnProperty(name = "app.gateway", havingValue = "feign")
class FeignGateway implements ApiGateway {

    private final EmployeeApiClient client;
    // J'ai supprimé batchClient ici car on ne s'en sert plus

    FeignGateway(EmployeeApiClient client) {
        System.out.println(">>> Using FeignGateway");
        this.client = client;
    }

    // --- Employees ---
    @Override
    public List<EmployeeDto> listEmployees() {
        return client.listEmployees();
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto e) {
        return client.createEmployee(e);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto e) {
        return client.updateEmployee(id, e);
    }

    @Override
    public void deleteEmployee(Long id) {
        client.deleteEmployee(id);
    }

    // --- Payslips ---
    @Override
    public List<PayslipDto> listPayslipsByEmployee(Long employeeId) {
        return client.listPayslipsByEmployee(employeeId);
    }


    @Override
    public EmployeeDto login(String email, String password) {
        Map<String,String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        return client.login(body);
    }
    // --- Postes (DB) ---
    @Override
    public List<PosteDto> listPoste() {
        return client.listPostes();
    }
    @Override
    public ResponseEntity<byte[]> downloadPayslip(Long payslipId) {
        return client.downloadPayslip(payslipId);
    }
    @Override
    public void upsertHours(Long employeeId, LocalDate month, Double week1, Double week2, Double week3, Double week4) {
        client.upsertHours(employeeId, month, week1, week2, week3, week4);
    }
        @Override
    public List<LeaveRequestDto> listLeaves(Long employeeId) {
        return client.listLeavesByEmployee(employeeId);
    }

    @Override
    public LeaveRequestDto createLeave(Long employeeId, LeaveRequestDto dto) {
        return client.createLeave(employeeId, dto);
    }
    @Override
    public List<LeaveRequestDto> listAllLeaves() {
        return client.getAllLeaves();
    }

    @Override
    public void updateLeaveStatus(Long id, String status) {
        client.updateLeaveStatus(id, status);
    }
     @Override
    public EmployeeDto getEmployee(Long id) {
        return client.getEmployee(id);
    }
    
    
}