package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;


@Slf4j
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "capsules")
public class Capsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capsule_name", nullable = false, length = 100)
    private String capsuleName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_id")
    private UserModel creator;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CapsuleStatus status;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "lock_date")
    private LocalDateTime lockDate;

    @Column(name = "open_date")
    private LocalDateTime openDateTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", unique = true, nullable = true)
    private Goal goal;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Memory> memoryEntries = new HashSet<>();

    // NEW: For Shared Capsules
    @Column(name = "is_shared", nullable = false)
    private Boolean isShared;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "capsule_shared_users",
        joinColumns = @JoinColumn(name = "capsule_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> sharedWithUsers = new HashSet<>();

    // NEW: Track users who are "ready" to close the capsule
    // This could also be a separate entity if more data is needed (e.g., date they became ready)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "capsule_ready_users",
        joinColumns = @JoinColumn(name = "capsule_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> readyToCloseUsers = new HashSet<>();


    // --- Constructors ---
    // Constructor matching fields used in fromDTOAndUser for clarity
    public Capsule(String capsuleName, UserModel creator, CapsuleStatus status, LocalDateTime creationDate, LocalDateTime lockDate, LocalDateTime openDateTime, boolean isShared, Set<UserModel> sharedWithUsers) {
        this.capsuleName = capsuleName;
        this.creator = creator;
        this.status = status;
        this.creationDate = creationDate;
        this.lockDate = lockDate;
        this.openDateTime = openDateTime;
        this.isShared = isShared;
        if (sharedWithUsers != null) {
            this.sharedWithUsers.addAll(sharedWithUsers);
        }
    }


    // --- Helper Methods for Bidirectional Relationship Management ---
    public void setGoal(Goal goal) {
        if (this.goal != null && !this.goal.equals(goal)) {
            this.goal.setCapsule(null);
        }
        this.goal = goal;
        if (goal != null) {
            goal.setCapsule(this);
        }
    }

    public void addMemory(Memory memory) {
        this.memoryEntries.add(memory);
        memory.setCapsule(this);
    }

    public void removeMemory(Memory memory) {
        this.memoryEntries.remove(memory);
        memory.setCapsule(null);
    }

    // NEW: Add/Remove shared users
    public void addSharedUser(UserModel user) {
        if(this.sharedWithUsers == null) {
            this.sharedWithUsers = new HashSet<>();
        }
        this.sharedWithUsers.add(user);
    }

    public void removeSharedUser(UserModel user) {
        this.sharedWithUsers.remove(user);
    }

    // NEW: Mark user as ready to close
    public void addReadyUser(UserModel user) {
        this.readyToCloseUsers.add(user);
    }

    public void removeReadyUser(UserModel user) {
        this.readyToCloseUsers.remove(user);
    }


    // --- Business Logic Methods ---
    public void changeCapsuleName(String newCapsuleName) {
        if (status != CapsuleStatus.CREATED) {
            throw new CapsuleHasBeenLocked("Cannot change the name of a capsule that is not in CREATED status.");
        }
        this.capsuleName = newCapsuleName;
    }

    public void open() {
        if (status != CapsuleStatus.CLOSED) {
            throw new CapsuleIsNotClosedYet("Trying to open a capsule which is not in CLOSED status.");
        }
        if (openDateTime == null || openDateTime.isAfter(LocalDateTime.now())) {
            throw new CapsuleIsNotClosedYet("Cannot open capsule before its scheduled open date.");
        }
        this.status = CapsuleStatus.OPEN;
    }

    private void throwIfNotEditable() {
        if (this.status == CapsuleStatus.CLOSED) {
            throw new CapsuleHasBeenLocked("Capsule is locked. Cannot perform this action.");
        }
        if (this.status == CapsuleStatus.OPEN) {
            throw new CapsuleIsOpened("Capsule is opened. Cannot perform this action.");
        }
    }

    public static Capsule fromDTOAndUser(CapsuleCreateDto capsuleCreateDto, UserModel creator, Set<UserModel> sharedUsers) {
        Capsule newCapsule = Capsule.builder()
            .capsuleName(capsuleCreateDto.getCapsuleName())
            .creator(creator)
            .status(CapsuleStatus.CREATED)
            .creationDate(LocalDateTime.now())
            .lockDate(null)
            .openDateTime(null)
            .isShared(sharedUsers != null && !sharedUsers.isEmpty()) // Set isShared based on provided users
            .build();

        // Set the goal
        Goal goal = Goal.builder()
            .content(capsuleCreateDto.getGoal().getContent())
            .creator(creator)
            .isAchieved(false) // Default to false on creation
            .isVisible(capsuleCreateDto.getGoal().getIsVisible())
            .creationDate(LocalDate.now())
            .capsule(newCapsule) // Set back-reference immediately
            .build();
        newCapsule.setGoal(goal); // Associate goal with capsule (will also set back-ref)

        // Add shared users
        if (sharedUsers != null) {
            log.info("Adding shared user: {}", newCapsule.getSharedWithUsers());
            sharedUsers.forEach(newCapsule::addSharedUser);
        }

        return newCapsule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (getClass() != o.getClass() && !(o instanceof HibernateProxy))) return false;
        Capsule capsule = (Capsule) (o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation() : o);
        return id != null && Objects.equals(id, capsule.id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
