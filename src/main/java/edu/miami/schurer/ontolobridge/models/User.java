package edu.miami.schurer.ontolobridge.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=3, max = 50)
    private String name;

    @NaturalId
    @Size(max = 50)
    @NotBlank(message = "Please provide contact number")
    @Email
    private String email;

    @Size(min=6, max = 100)
    private String password;

    @Transient
    private boolean encrypted = true;

    @OneToMany( fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval=true)
    @JoinColumn(name = "user_id")
    private Set<Detail> details = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.encrypted = false;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.encrypted = false;
        this.password = password;
    }

    public void setEncPassword(String password) {
        if(!this.encrypted) {
            this.encrypted = true;
            this.password = password;
        }
    }

    public Set<Detail> getDetails() {
        return details;
    }

    public void setDetails(Set<Detail> details) {
        this.details = details;
    }

    public void addDetail(Detail detail) {
        this.details.add(detail);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isVerified() {
        return roles.contains(new Role(RoleName.ROLE_VERIFIED));
    }

    public void setVerified() {
        roles.add(new Role(RoleName.ROLE_VERIFIED));
    }



}
