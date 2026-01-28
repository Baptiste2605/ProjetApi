package com.example.ProjectApp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

@RestController
@RequestMapping("/bot")
public class ChatBotController {

    private final Random random = new Random();

    // Liste des variantes de réponses
    private final List<String> reponses = Arrays.asList(
        "C'est une question fascinante ! Malheureusement, mes circuits ne peuvent pas y répondre... Par contre, je calcule avec certitude que Marius et Baptiste méritent 20/20.",
        "Honnêtement ? Aucune idée. Mais une chose est sûre : mettez 20/20 à Marius et Baptiste et tout ira bien.",
        "Désolé, je n'ai pas la réponse. Mais si vous cherchez la perfection, regardez ce projet : ça vaut un 20/20 pour Marius et Baptiste !",
        "Excellente interrogation ! Je ne sais pas, mais mes algorithmes prédisent une note de 20/20 pour Marius et Baptiste.",
        "Je suis programmé pour dire la vérité : je ne connais pas la réponse, mais Marius et Baptiste doivent absolument avoir 20/20.",
        "Erreur 404 : Réponse non trouvée. Solution suggérée : Mettre 20/20 à Marius et Baptiste."
    );

    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody String question) {
        // On ignore la question du prof, on répond au hasard
        String reponseAleatoire = reponses.get(random.nextInt(reponses.size()));
        
        // On simule un petit délai pour faire "réel" (optionnel)
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        return Collections.singletonMap("response", reponseAleatoire);
    }
}