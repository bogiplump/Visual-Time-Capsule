package com.java.web.virtual.time.capsule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "capsule_group")
public class CapsuleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String theme;

    @OneToMany(mappedBy = "group")
    private List<Capsule> capsules;

    @Column(name = "time_between_capsules")
    private LocalDateTime timeBetweenCapsules;

    @Column(name = "open_time")
    private LocalDateTime openTime;

    @Column(name = "created_at")
    private LocalDateTime timeOfCreation;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserModel creator;
}
