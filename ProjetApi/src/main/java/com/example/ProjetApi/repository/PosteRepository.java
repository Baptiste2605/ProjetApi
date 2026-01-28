package com.example.ProjetApi.repository;

import com.example.ProjetApi.model.Poste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PosteRepository extends JpaRepository<Poste, Long> {
    Optional<Poste> findByName(String name);
}
