package com.example.ProjetApi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private Integer soldeCP;
    private Integer soldeRTT;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    // --- RELATION VERS LA BASE DE DONNÉES (Le vrai objet) ---
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;

    @Transient 
    private Long posteId;

    // --- CONSTRUCTEURS ---

    // Constructeur vide obligatoire pour JPA
    public Employee() {
        this.soldeCP = 25;  // Valeurs par défaut
        this.soldeRTT = 10;
    }

    public Employee(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.soldeCP = 25;
        this.soldeRTT = 10;
    }

    // --- GETTERS ET SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getSoldeCP() {
        return soldeCP;
    }

    public void setSoldeCP(Integer soldeCP) {
        this.soldeCP = soldeCP;
    }

    public Integer getSoldeRTT() {
        return soldeRTT;
    }

    public void setSoldeRTT(Integer soldeRTT) {
        this.soldeRTT = soldeRTT;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public Long getPosteId() {
        return posteId;
    }

    public void setPosteId(Long posteId) {
        this.posteId = posteId;
    }
}