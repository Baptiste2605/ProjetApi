package com.example.ProjetApi.controller;

import com.example.ProjetApi.model.LeaveRequest;
import com.example.ProjetApi.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leave Requests API")
public class LeaveRequestController {

    private final LeaveRequestService service;

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

    // 1. Liste globale (Pour les RH uniquement)
    @GetMapping
    public List<LeaveRequest> listAll() {
        return service.getAll();
    }

    // 2. Liste filtrée (Pour l'employé connecté)
    // C'est cette URL que le Frontend "Employé" doit appeler !
    @GetMapping("/employee/{empId}")
    public List<LeaveRequest> listByEmployee(@PathVariable Long empId) {
        return service.getByEmployee(empId);
    }

    // 3. Création
    @PostMapping("/employee/{empId}")
    public LeaveRequest create(@PathVariable Long empId, @RequestBody LeaveRequest req) {
        req.setEmployeeId(empId);
        return service.create(req);
    }

    
    @PutMapping("/{id}")
    public LeaveRequest updateStatus(@PathVariable Long id, @RequestParam String status) {
        LeaveRequest updateInfo = new LeaveRequest();
        updateInfo.setStatus(status);
        return service.update(id, updateInfo);
    }
}