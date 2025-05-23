package com.java.web.virtual.time.capsule.model.entity;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import java.util.Date;
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
    @GeneratedValue
    private Integer id;

    @Column(name = "capsule_name")
    private String capsuleName;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "lock_date")
    private Date lockDate;

    @Column(name = "open_date")
    private Date openDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CapsuleStatus capsuleStatus;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserEntity creator;

    @OneToOne
    @JoinColumn(name = "goal_id")
    private GoalEntity goal;

    @OneToMany(mappedBy = "capsule")
    private Set<MemoryEntity> memoryEntries;
}
