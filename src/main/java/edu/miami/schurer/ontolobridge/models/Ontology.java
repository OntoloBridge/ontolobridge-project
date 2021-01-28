package edu.miami.schurer.ontolobridge.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ontologies")
public class Ontology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;
    private String ontology_short;
    private String seperator;
    private String padding;

    public Ontology(Long id, String name, String url, String ontology_short, String seperator, String padding) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.ontology_short = ontology_short;
        this.seperator = seperator;
        this.padding = padding;
    }

    public Ontology() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
