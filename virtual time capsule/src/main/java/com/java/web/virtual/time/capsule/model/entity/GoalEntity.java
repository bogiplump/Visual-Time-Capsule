package com.java.web.virtual.time.capsule.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Date;

import lombok.Data;

@Data
@Entity
@Table(name = "goals")
public class GoalEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "is_visible")
    private boolean isVisible;
    @Column(name = "is_achieved")
    private boolean isAchieved;
    @Column(name = "created_at")
    private Date creationDate;

    private String content;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserEntity creator;

    @OneToOne(mappedBy = "goal")
    private CapsuleEntity capsule;
}
