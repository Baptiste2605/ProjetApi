package com.example.ProjetApi.controller;


import com.example.ProjetApi.model.Employee;
import com.example.ProjetApi.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee API")
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }

    @GetMapping
    public List<Employee> list() { return service.getAll(); }

    @PostMapping
    public Employee create(@RequestBody Employee e) { return service.create(e); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
    // EmployeeController.java
    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @RequestBody Employee in) {
        return service.update(id, in);
    }
    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return service.getById(id);
    }

}
