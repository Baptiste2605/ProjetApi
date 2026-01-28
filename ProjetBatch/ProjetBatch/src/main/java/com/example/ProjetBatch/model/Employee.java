package com.example.ProjetBatch.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id") // FK vers poste(id)
    private Poste poste;

    public Employee() {}

    public Employee(String firstName, String lastName, String email, Poste poste) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.poste = poste;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Poste getPoste() { return poste; }
    public void setPoste(Poste poste) { this.poste = poste; }


}