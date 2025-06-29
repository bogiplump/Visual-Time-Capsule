package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
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
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Add Setter if you use it or need it for internal logic, otherwise just Getter
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

@Slf4j
@Entity
@Getter // Use @Getter at class level for all fields to have getters
@Setter // Use @Setter at class level if you want setters for all fields
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Good practice for JPA entities
@AllArgsConstructor
@Table(name = "capsules")
public class Capsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capsule_name", nullable = false, length = 100) // Increased length for flexibility
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

    // This is the OWNING side of the @OneToOne relationship from Capsule's perspective.
    // The foreign key (goal_id) will be in the 'capsules' table.
    // CascadeType.ALL and orphanRemoval=true ensure that when a Capsule is deleted,
    // its associated Goal is also deleted.
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", unique = true, nullable = true) // 'nullable = true' if a Capsule can exist without a Goal initially
    private Goal goal;

    // This remains the same: Capsule owns Memories, which are deleted when Capsule is deleted.
    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Memory> memoryEntries = new HashSet<>();


    // This method ensures capsule is CREATED before changing name
    public void changeCapsuleName(String newCapsuleName) {
        if (status != CapsuleStatus.CREATED) {
            throw new CapsuleHasBeenLocked("Cannot change the name of a capsule that is not in CREATED status.");
        }
        this.capsuleName = newCapsuleName;
    }

    // This method needs to be called in a service when updating the goal's association or content
    public void setGoal(Goal goal) {
        if (this.goal != null && !this.goal.equals(goal)) {
            // Remove old back-reference if existing goal is being replaced
            this.goal.setCapsule(null);
        }
        this.goal = goal;
        if (goal != null) {
            goal.setCapsule(this); // Set the back-reference
        }
    }

    // This method needs to be called in a service when adding/removing memories
    public void addMemory(Memory memory) {
        // This is where you would call checkIfCapsuleEditable() if you want to prevent
        // adding memories to CLOSED or OPEN capsules.
        // checkIfCapsuleEditable(); // <-- Call here or in CapsuleService.saveMemory
        this.memoryEntries.add(memory);
        memory.setCapsule(this);
    }

    public void removeMemory(Memory memory) {
        // This is where you would call checkIfCapsuleEditable() if you want to prevent
        // removing memories from CLOSED or OPEN capsules.
        // checkIfCapsuleEditable(); // <-- Call here or in CapsuleService.deleteMemory
        this.memoryEntries.remove(memory);
        memory.setCapsule(null); // Detach
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

    // This method is now ONLY for internal validation where needed, NOT in getters
    // It is `private` because it's an internal helper
    private void throwIfNotEditable() { // Renamed for clarity: it throws if NOT editable
        if (this.status == CapsuleStatus.CLOSED) {
            throw new CapsuleHasBeenLocked("Capsule is locked. Cannot perform this action.");
        }
        if (this.status == CapsuleStatus.OPEN) {
            throw new CapsuleIsOpened("Capsule is opened. Cannot perform this action.");
        }
    }

    public static Capsule fromDTOAndUser(CapsuleCreateDto capsuleCreateDto, UserModel creator) {
        GoalDto goalDto = capsuleCreateDto.getGoal();
        Goal goal = Goal.builder()
            .content(goalDto.getContent())
            .creator(creator)
            .isAchieved(goalDto.isAchieved())
            .isVisible(goalDto.isVisible())
            .creationDate(LocalDate.now())
            .build();

        // Create the capsule
        Capsule newCapsule = new Capsule(
            capsuleCreateDto.getCapsuleName(),
            creator,
            CapsuleStatus.CREATED,
            LocalDateTime.now(), // creationDate
            null, // lockDate
            null // openDateTime
        );
        newCapsule.setGoal(goal); // Associate goal with capsule

        return newCapsule;
    }

    // You might need a more comprehensive constructor if using @AllArgsConstructor with specific fields
    // This constructor matches the fields used in fromDTOAndUser for clarity
    public Capsule(String capsuleName, UserModel creator, CapsuleStatus status, LocalDateTime creationDate, LocalDateTime lockDate, LocalDateTime openDateTime) {
        this.capsuleName = capsuleName;
        this.creator = creator;
        this.status = status;
        this.creationDate = creationDate;
        this.lockDate = lockDate;
        this.openDateTime = openDateTime;
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
        // Alternatively, if IDs are generated very early and are unique:
        // return id != null ? id.hashCode() : 0;
    }

}