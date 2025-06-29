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
@AllArgsConstructor
@Table(name = "capsule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @ManyToOne
    @JoinColumn(name = "capsule_group_id")
    private CapsuleGroup group;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", unique = true, nullable = true)
    private Goal goal;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Memory> memoryEntries = new HashSet<>();

    @Column(name = "is_shared", nullable = false)
    private Boolean isShared;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "capsule_shared_users",
        joinColumns = @JoinColumn(name = "capsule_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> sharedWithUsers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "capsule_ready_users",
        joinColumns = @JoinColumn(name = "capsule_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> readyToCloseUsers = new HashSet<>();


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

    
    public void setGoal(Goal goal) {
        throwIfNotEditable();
        if (this.goal != null && !this.goal.equals(goal)) {
            this.goal.setCapsule(null);
        }
        this.goal = goal;
        if (goal != null) {
            goal.setCapsule(this);
        }
    }

    public void addMemory(Memory memory) {
        throwIfNotEditable();
        this.memoryEntries.add(memory);
        memory.setCapsule(this);
    }

    public void removeMemory(Memory memory) {
        throwIfNotEditable();
        this.memoryEntries.remove(memory);
        memory.setCapsule(null);
    }

    
    public void addSharedUser(UserModel user) {
        throwIfNotEditable();
        if(this.sharedWithUsers == null) {
            this.sharedWithUsers = new HashSet<>();
        }
        this.sharedWithUsers.add(user);
    }

    
    public void addReadyUser(UserModel user) {
        throwIfNotEditable();
        this.readyToCloseUsers.add(user);
    }

    public void removeReadyUser(UserModel user) {
        throwIfNotEditable();
        this.readyToCloseUsers.remove(user);
    }



    public void changeCapsuleName(String newCapsuleName) {
        throwIfNotEditable();
        if (status != CapsuleStatus.CREATED) {
            throw new CapsuleHasBeenLocked("Cannot change the name of a capsule that is not in CREATED status.");
        }
        this.capsuleName = newCapsuleName;
    }

    public void open() {
        throwIfNotEditable();
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
