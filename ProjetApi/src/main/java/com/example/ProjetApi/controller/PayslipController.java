package com.example.ProjetApi.controller;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.example.ProjetApi.model.Payslip;
import com.example.ProjetApi.service.PayslipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@Tag(name = "Payslip API")
public class PayslipController {
    private final PayslipService service;
    public PayslipController(PayslipService service) { this.service = service; }

    // exactement ce que ton Feign attend
    @GetMapping("/by-employee/{employeeId}")
    public List<Payslip> byEmployee(@PathVariable Long employeeId) {
        return service.listByEmployee(employeeId);
    }
@GetMapping(value = "/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {
        var p = service.getById(id);
        if (p == null || p.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        String filenameOnly = new java.io.File(p.getFilePath()).getName();
        

        Path path = Paths.get("/app/payslips", filenameOnly);

       

        if (!Files.exists(path)) {
            // Petit debug pour voir dans les logs si ça plante
            System.err.println("Fichier introuvable ici : " + path.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        String fileName = (p.getFileName() != null && !p.getFileName().isBlank())
                ? p.getFileName()
                : filenameOnly;

        String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = MediaType.APPLICATION_PDF_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

}
