package com.java.web.virtual.time.capsule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "goals")
public class GoalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Column(name = "is_achieved", nullable = false)
    private boolean isAchieved;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate creationDate;

    private String content;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private CapsuleUser creator;

    @OneToOne
    private CapsuleEntity capsule;
}
