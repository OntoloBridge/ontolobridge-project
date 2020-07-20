package edu.miami.schurer.ontolobridge.models;

import javax.persistence.*;

@Entity
@Table(name = "user_details")
public class Detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="field")
    private String field;

    @Column(name="value")
    private String value;

    public Detail() {}

    public Detail(String Field,String Value) {
        this.field = Field;
        this.value = Value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {this.value = value;
    }

}