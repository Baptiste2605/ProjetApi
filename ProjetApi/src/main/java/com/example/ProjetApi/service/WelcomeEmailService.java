package com.example.ProjetApi.service;

import com.example.ProjetApi.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class WelcomeEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAccountInfo(Employee employee, String clearPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("baptisted989@gmail.com"); 
            message.setTo(employee.getEmail());
            message.setSubject("Bienvenue chez MyPaiRH - Vos identifiants");

            String corpsDuMessage = "Bonjour " + employee.getFirstName() + " " + employee.getLastName() + ",\n\n"
                    + "Bienvenue dans l'équipe ! Nous sommes ravis de vous accueillir au poste de : " + employee.getPoste() + ".\n\n" 
                    + "Voici vos identifiants pour vous connecter à votre espace personnel :\n"
                    + "------------------------------------------------\n"
                    + "👤 Email : " + employee.getEmail() + "\n"
                    + "🔑 Mot de passe : " + clearPassword + "\n"
                    + "------------------------------------------------\n\n"
                    + "Merci de conserver ces informations précieusement.\n\n"
                    + "Cordialement,\n"
                    + "Le service Ressources Humaines.";

            message.setText(corpsDuMessage);
            mailSender.send(message);
            System.out.println("Mail de bienvenue envoyé à " + employee.getEmail());

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du mail : " + e.getMessage());
        }
    }
}