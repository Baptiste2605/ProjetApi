package com.example.ProjetBatch.service;

import com.example.ProjetBatch.model.Employee;
import com.example.ProjetBatch.model.Payslip;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired; //
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class SmtpEmailService implements EmailService {

    // On laisse Spring faire l'injection (comme dans l'API)
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendPayslip(Employee emp, Payslip p, File pdf) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            
            h.setFrom("baptisted989@gmail.com");
            h.setTo(emp.getEmail());
            h.setSubject("Fiche de paie - " + p.getMonth());
            h.setText("Bonjour " + emp.getFirstName() + ",\n\nVeuillez trouver ci-joint votre bulletin de paie.\n\nCordialement,\nService RH.");
            
            h.addAttachment(pdf.getName(), new FileSystemResource(pdf));

            mailSender.send(msg);
            System.out.println("   📧 Email envoyé à " + emp.getEmail());

        } catch (Exception e) {
            // On garde l'erreur détaillée pour le debug si besoin
            throw new RuntimeException("Erreur envoi mail: " + e.getMessage());
        }
    }
}