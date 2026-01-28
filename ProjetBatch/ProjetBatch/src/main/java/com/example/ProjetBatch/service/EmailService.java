package com.example.ProjetBatch.service;

import com.example.ProjetBatch.model.Employee;
import com.example.ProjetBatch.model.Payslip;

import java.io.File;

public interface EmailService {
    void sendPayslip(Employee emp, Payslip p, File pdfFile);
}
