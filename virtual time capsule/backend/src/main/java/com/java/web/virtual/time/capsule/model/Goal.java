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
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goal")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;

    @Column(name = "is_achieved", nullable = false)
    private Boolean isAchieved;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime creationDate;

    private String content;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserModel creator;

    @OneToOne(mappedBy = "goal")
    private Capsule capsule;

}
