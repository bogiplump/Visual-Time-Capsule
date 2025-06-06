package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "capsules")
public class CapsuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capsule_name", nullable = false)
    private String capsuleName;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "lock_date")
    private LocalDate lockDate;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CapsuleStatus capsuleStatus;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private CapsuleUser creator;

    @OneToOne
    @JoinColumn(name = "goal_id")
    private GoalEntity goal;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private CapsuleGroupEntity group;
}
