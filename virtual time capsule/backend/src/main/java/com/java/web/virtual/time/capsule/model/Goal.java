package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.GoalDto;
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
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Column(name = "is_achieved", nullable = false)
    private boolean isAchieved;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate creationDate;

    private String content;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserModel creator;

    @OneToOne
    private Capsule capsule;

    public static Goal fromDTO(GoalDto goalDto, UserModel creator) {

        return Goal
            .builder()
            .creator(creator)
            .isVisible(goalDto.isVisible())
            .isAchieved(false)
            .content(goalDto.getContent())
            .creationDate(LocalDate.now())
            .content(goalDto.getContent())
            .build();
    }
}
