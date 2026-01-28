package com.example.ProjectApp.model;

import java.time.LocalDate;

public class NewsDto {

    private String title;

    private LocalDate date;

    private String category;

    private String content;

    private String styleClass;

    private String imagePath;

    public NewsDto(String title, LocalDate date, String category, String content, String styleClass, String imagePath) {

        this.title = title;

        this.date = date;

        this.category = category;

        this.content = content;

        this.styleClass = styleClass;

        this.imagePath = imagePath;

    }

    // Getters

    public String getTitle() { return title; }

    public LocalDate getDate() { return date; }

    public String getCategory() { return category; }

    public String getContent() { return content; }

    public String getStyleClass() { return styleClass; }

    public String getImagePath() { return imagePath; } // <--- NOUVEAU GETTER

}

