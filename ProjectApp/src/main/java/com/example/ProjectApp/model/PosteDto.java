package com.example.ProjectApp.model;

public class PosteDto {
    private Long id;
    private String name;
    private double hourlyRate; // AJOUTER CE CHAMP

    public PosteDto() {}

    public PosteDto(Long id, String name, double hourlyRate) {
        this.id = id;
        this.name = name;
        this.hourlyRate = hourlyRate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // AJOUTER CES GETTERS/SETTERS
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
}