package com.example.ProjectApp.model;

public class EmployeeDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer soldeCP;
    private Integer soldeRTT;

    // on remplace "role" (String libre) par un vrai poste
    private String password;
    private PosteDto poste;
    private Long posteId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public PosteDto getPoste() { return poste; }
    public void setPoste(PosteDto poste) { this.poste = poste; }

    public Long getPosteId() { return posteId; }
    public void setPosteId(Long posteId) { this.posteId = posteId; }

    public Integer getSoldeCP() { return soldeCP; }
    public void setSoldeCP(Integer soldeCP) { this.soldeCP = soldeCP; }
    public Integer getSoldeRTT() { return soldeRTT; }
    public void setSoldeRTT(Integer soldeRTT) { this.soldeRTT = soldeRTT; }

}
