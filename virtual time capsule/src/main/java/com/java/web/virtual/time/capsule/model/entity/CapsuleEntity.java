package com.java.web.virtual.time.capsule.model.entity;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;

import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
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

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "capsules")
public class CapsuleEntity {
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
    private UserEntity creator;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private GoalEntity goal;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Set<MemoryEntity> memoryEntries;

    public CapsuleEntity(String capsuleName, UserEntity creator,
                         GoalEntity goal, Set<MemoryEntity> memoryEntries) {
        if (capsuleName == null || creator == null ||
            goal == null || memoryEntries == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity constructor. ");
        }

        this.capsuleName = capsuleName;
        this.creator = creator;
        this.goal = goal;
        this.memoryEntries = memoryEntries;

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
            throw new CapsuleHasBeenLocked("Can not view or add memories in a locked capsule");
        }
        if (status == CapsuleStatus.OPEN) {
            throw new CapsuleIsOpened("Can not add memories after a capsule has been locked and opened");
        }
    }

    public void setMemoryEntries(Set<MemoryEntity> memoryEntries) {
        if (memoryEntries == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::setMemoryEntries(). ");
        }

        checkIfCapsuleEditable();

        this.memoryEntries = memoryEntries;
    }

    public void addMemory(MemoryEntity memory) {
        if (memory == null) {
            throw new IllegalArgumentException("Null reference in CapsuleEntity::addMemory(). ");
        }

        checkIfCapsuleEditable();

        this.memoryEntries.add(memory);
    }

    public Set<MemoryEntity> getMemoryEntries() {
        checkIfCapsuleEditable();

        return Set.copyOf(memoryEntries);
    }

    public boolean isTimeToOpen() {
        return openDate.isBefore(LocalDateTime.now());
    }

    public void open() {
        status = CapsuleStatus.OPEN;
    }
}
