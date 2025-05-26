package com.java.web.virtual.time.capsule.model.entity;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CapsuleStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "lock_date")
    private LocalDateTime lockDate;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity creator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private GoalEntity goal;

    @OneToMany(mappedBy = "capsule")
    private Set<MemoryEntity> memoryEntries;
}
