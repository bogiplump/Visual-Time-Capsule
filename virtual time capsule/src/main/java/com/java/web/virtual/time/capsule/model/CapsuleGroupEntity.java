package com.java.web.virtual.time.capsule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "capsule_groups")
public class CapsuleGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String theme;

    @OneToOne
    @JoinColumn(name = "first_capsule_node_id")
    private CapsuleNodeEntity firstCapsuleNode;

    @Column(name = "time_between_capsules")
    private LocalDateTime timeBetweenCapsules;

    @Column(name = "open_time")
    private LocalDateTime openTime;
}