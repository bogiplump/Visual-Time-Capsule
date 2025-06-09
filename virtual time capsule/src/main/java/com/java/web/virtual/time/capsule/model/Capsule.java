package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.capsule.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.goal.GoalDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;

import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleIsNotClosedYet;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleIsOpened;
import jakarta.persistence.CascadeType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "capsules")
public class Capsule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "capsule_name", nullable = false)
    private String capsuleName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private CapsuleStatus status;

    @Column(name = "creation_date", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime creationDate;

    @Column(name = "lock_date")
    @Setter(AccessLevel.NONE)
    private LocalDateTime lockDate;

    @Column(name = "open_date")
    @Setter(AccessLevel.NONE)
    private LocalDateTime openDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User creator;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    @Setter(AccessLevel.NONE)
    private Goal goal;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Set<Memory> memoryEntries;

    public Capsule(String capsuleName, User creator,
                   Goal goal) {
        if (capsuleName == null || creator == null ||
            goal == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity constructor. ");
        }

        this.capsuleName = capsuleName;
        this.creator = creator;
        this.goal = goal;
        this.memoryEntries = new HashSet<>();

        this.status = CapsuleStatus.CREATED;
        this.creationDate = LocalDateTime.now();
    }

    public void lock(LocalDateTime openDate) {
        lockDate = LocalDateTime.now();
        status = CapsuleStatus.CLOSED;
        this.openDate = openDate;
    }

    private void checkIfCapsuleEditable() {
        if (status == CapsuleStatus.CLOSED) {
            throw new CapsuleHasBeenLocked("Can not view or add memories or change the goal of a locked capsule");
        }
        if (status == CapsuleStatus.OPEN) {
            throw new CapsuleIsOpened("Can not add memories or change the capsules goal after a capsule has been locked and opened");
        }
    }

    public void setMemoryEntries(Set<Memory> memoryEntries) {
        if (memoryEntries == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::setMemoryEntries(). ");
        }

        checkIfCapsuleEditable();

        this.memoryEntries = memoryEntries;
    }

    public void addMemory(Memory memory) {
        if (memory == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::addMemory(). ");
        }

        checkIfCapsuleEditable();

        this.memoryEntries.add(memory);
    }

    public void removeMemory(Memory memory) {
        if (memory == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::removeMemory(). ");
        }

        checkIfCapsuleEditable();

        this.memoryEntries.remove(memory);
    }

    public void setGoal(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::setGoal");
        }

        checkIfCapsuleEditable();

        this.goal = goal;
        goal.setCapsule(this);
    }

    public void setOpenDate(LocalDateTime openDate) {
        if (openDate == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::setOpenDate");
        }

        switch (status) {
            case CapsuleStatus.CREATED ->  throw new CapsuleIsNotClosedYet("Trying to change the openDate of a capsule which is not locked");
            case CapsuleStatus.OPEN -> throw new CapsuleIsOpened("Trying to change the openDate of opened capsule");
            case CapsuleStatus.CLOSED ->  this.openDate = openDate;
        }
    }

    public Set<Memory> getMemoryEntries() {
        checkIfCapsuleEditable();

        return memoryEntries;
    }

    public boolean isTimeToOpen() {
        return openDate.isBefore(LocalDateTime.now());
    }

    public void open() {
        switch (status) {
            case CapsuleStatus.CREATED ->  throw new CapsuleIsNotClosedYet("Trying to open a capsule which is not locked");
            case CapsuleStatus.OPEN -> throw new CapsuleIsOpened("Trying to open an opened capsule");
            case CapsuleStatus.CLOSED ->  status = CapsuleStatus.OPEN;
        }
    }

    public CapsuleResponseDto toCapsuleResponseDto() {
        return null; //TODO implement
    }

    public static Capsule fromDTOAndUser(CapsuleCreateDto capsuleCreateDto, User creator) {
        GoalDto goalDto = capsuleCreateDto.getGoal();
        Goal goal = Goal.builder()
            .content(goalDto.getContent())
            .creator(creator)
            .isAchieved(goalDto.isAchieved())
            .isVisible(goalDto.isVisible())
            .creationDate(LocalDate.now())
            .build();
        return new Capsule(
            capsuleCreateDto.getCapsuleName(),
            creator,
            goal
        );
    }
}
