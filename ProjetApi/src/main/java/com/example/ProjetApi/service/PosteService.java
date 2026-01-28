package com.example.ProjetApi.service;

import com.example.ProjetApi.model.Poste;
import com.example.ProjetApi.repository.PosteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PosteService {

    private final PosteRepository repo;

    public PosteService(PosteRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Poste> listAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Poste getOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Poste not found: " + id));
    }

    @Transactional
    public Poste create(Poste p) {

        return repo.save(p);
    }

    @Transactional
    public Poste update(Long id, Poste p) {
        Poste cur = getOrThrow(id);
        // if name changed, check uniqueness
        if (!cur.getName().equalsIgnoreCase(p.getName()))
        {
            throw new IllegalArgumentException("Poste name already exists: " + p.getName());
        }
        cur.setName(p.getName());
        cur.setHourlyRate(p.getHourlyRate());
        return repo.save(cur);
    }

    @Transactional
    public void delete(Long id) {
        // if you want to protect from FK constraints, check usage before delete
        repo.deleteById(id);
    }
}
