package com.java.web.virtual.time.capsule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "family_name")
    private String lastName;

    @Column(name = "created_at")
    private LocalDate creationDate;

    private String username;
    private String password;
    private String email;

    @OneToMany(mappedBy = "creator")
    private Set<Goal> goals;

    @OneToMany(mappedBy = "creator")
    private Set<Memory> memoryEntries;

    @OneToMany(mappedBy = "creator")
    private Set<Capsule> capsules;
}
