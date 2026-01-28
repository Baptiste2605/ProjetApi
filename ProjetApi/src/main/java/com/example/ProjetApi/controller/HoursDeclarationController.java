// src/main/java/com/example/ProjetApi/controller/HoursDeclarationController.java
package com.example.ProjetApi.controller;
 
import com.example.ProjetApi.model.HoursDeclaration;
import com.example.ProjetApi.service.HoursDeclarationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
 
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/hours")
public class HoursDeclarationController {
 
    private final HoursDeclarationService service;
 
    public HoursDeclarationController(HoursDeclarationService service) {
        this.service = service;
    }
 
    // POST /api/hours  (query params ou x-www-form-urlencoded)
    @PostMapping
    public HoursDeclaration upsert(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("month")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
            @RequestParam(value = "week1", required = false) Double week1,
            @RequestParam(value = "week2", required = false) Double week2,
            @RequestParam(value = "week3", required = false) Double week3,
            @RequestParam(value = "week4", required = false) Double week4
    ) {
        LocalDate m = month.withDayOfMonth(1); // normalise au 1er du mois
        return service.upsert(employeeId, m, week1, week2, week3, week4);
    }
 
    // GET /api/hours/by-employee/{employeeId}
    @GetMapping("/by-employee/{employeeId}")
    public List<HoursDeclaration> list(@PathVariable Long employeeId) {
        return service.listByEmployee(employeeId);
    }
 
    // GET /api/hours/total?employeeId=...&month=2025-12   (ou 2025-12-01)
    @GetMapping("/total")
    public Map<String, Object> total(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("month") String monthParam
    ) {
        LocalDate month = parseMonthToFirstDay(monthParam);
        HoursDeclaration hd = service.upsert(employeeId, month, null, null, null, null);
        return Map.of(
                "employeeId", employeeId,
                "month", month.toString(),
                "totalHours", hd.getTotalHours()
        );
    }
 
    // -------- Utils --------
    private static LocalDate parseMonthToFirstDay(String s) {
        String t = s.trim();
        if (t.length() == 7) { // "yyyy-MM"
            YearMonth ym = YearMonth.parse(t);
            return ym.atDay(1);
        }
        LocalDate d = LocalDate.parse(t); // "yyyy-MM-dd"
        return d.withDayOfMonth(1);
    }
}
 
 