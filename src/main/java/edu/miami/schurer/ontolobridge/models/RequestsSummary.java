package edu.miami.schurer.ontolobridge.models;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class RequestsSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=2, max = 50)
    private String Label;

    @NaturalId
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min=2, max = 100)
    private String ontology;

    @NotBlank
    @Size(min=2, max = 100)
    private Timestamp date;

    @NotBlank
    @Size(min=2, max = 100)
    private String status;

    public RequestsSummary(Long id, @NotBlank @Size(min = 2, max = 50) String label, @NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(min = 2, max = 100) String ontology, @NotBlank @Size(min = 2, max = 100) Timestamp date, @NotBlank @Size(min = 2, max = 100) String status) {
        this.id = id;
        Label = label;
        this.email = email;
        this.ontology = ontology;
        this.date = date;
        this.status = status;
    }

    public RequestsSummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
