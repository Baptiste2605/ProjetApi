package com.example.ProjetApi.controller;

import com.example.ProjetApi.model.Poste;
import com.example.ProjetApi.repository.PosteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postes")
@CrossOrigin(origins = "*") // si ton front tourne sur un autre port
public class PosteController {

    private final PosteRepository posteRepository;

    public PosteController(PosteRepository posteRepository) {
        this.posteRepository = posteRepository;
    }

    @GetMapping
    public List<Poste> list() {
        return posteRepository.findAll();
    }
}
