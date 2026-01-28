package com.example.ProjetApi.config;

import com.example.ProjetApi.model.Poste;
import com.example.ProjetApi.repository.PosteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PosteInitializer implements CommandLineRunner {

    private final PosteRepository posteRepository;

    public PosteInitializer(PosteRepository posteRepository) {
        this.posteRepository = posteRepository;
    }

    @Override
    public void run(String... args) {

        Map<String, Double> seedData = Map.ofEntries(
                // Direction & Management
                Map.entry("Directeur d'agence", 48.0),
                Map.entry("Responsable commercial", 40.0),
                Map.entry("Chef de projet digital", 32.0),
                Map.entry("Responsable conformité", 38.0),
                Map.entry("Directeur des risques", 55.0),
                Map.entry("Responsable ressources humaines", 37.0),

                // Conseil & Relation Client
                Map.entry("Conseiller clientèle particuliers", 23.0),
                Map.entry("Conseiller clientèle professionnels", 26.0),
                Map.entry("Chargé d'affaires entreprises", 33.0),
                Map.entry("Conseiller en gestion de patrimoine", 36.0),
                Map.entry("Chargé d'accueil bancaire", 18.0),
                Map.entry("Chargé de recouvrement", 24.0),

                // Finance & Marchés
                Map.entry("Analyste financier", 30.0),
                Map.entry("Trader", 50.0),
                Map.entry("Contrôleur de gestion", 28.0),
                Map.entry("Auditeur interne", 34.0),
                Map.entry("Chargé de conformité", 27.0),
                Map.entry("Risk manager", 42.0),

                // Informatique & Données
                Map.entry("Data analyst", 30.0),
                Map.entry("Data scientist", 40.0),
                Map.entry("Développeur applicatif", 32.0),
                Map.entry("Architecte SI", 48.0),
                Map.entry("Chef de projet IT", 38.0),
                Map.entry("Administrateur systèmes", 28.0),

                // Support & Opérations
                Map.entry("Chargé de back-office", 20.0),
                Map.entry("Assistant administratif", 18.0),
                Map.entry("Juriste bancaire", 35.0),
                Map.entry("Chargé de conformité KYC", 25.0),
                Map.entry("Contrôleur permanent", 29.0),
                Map.entry("Comptable", 27.0)
        );

        seedData.forEach((name, rate) -> {
            boolean exists = posteRepository.findByName(name).isPresent();
            if (!exists) {
                posteRepository.save(new Poste(name, rate));
            }
        });
    }
}
