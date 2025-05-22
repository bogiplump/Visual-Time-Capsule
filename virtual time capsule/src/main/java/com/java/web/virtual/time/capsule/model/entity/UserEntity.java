package com.java.web.virtual.time.capsule.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.Set;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "family_name")
    private String lastName;
    @Column(name = "created_at")
    private Date creationDate;

    private String username;
    private String password;
    private String email;

    @OneToMany(mappedBy = "creator")
    private Set<GoalEntity> goals;

//    @OneToMany(mappedBy = "created_by_id")
//    private Set<MemoryEntity> memoryEntries;

    @OneToMany(mappedBy = "creator")
    private Set<CapsuleEntity> capsules;
}
