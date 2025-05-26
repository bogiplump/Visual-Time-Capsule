package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.annotations.ValidEmail;
import com.java.web.virtual.time.capsule.annotations.ValidName;
import com.java.web.virtual.time.capsule.annotations.ValidPassword;
import com.java.web.virtual.time.capsule.annotations.ValidUsername;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
public class CapsuleUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ValidUsername
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ValidName
    @Column(nullable = false)
    private String firstName;

    @ValidName
    @Column(nullable = false)
    private String lastName;

    @ValidEmail
    @Column(updatable = false, nullable = false, unique = true)
    private String email;

    @NotNull(message = "Creation date cannot be null")
    private LocalDate creationDate;

    public CapsuleUser() {

    }

}
