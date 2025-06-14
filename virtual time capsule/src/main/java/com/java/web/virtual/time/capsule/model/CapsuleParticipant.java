package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.sharedcapsule.CapsuleParticipantResponseDto;
import com.java.web.virtual.time.capsule.repository.CapsuleParticipantRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "capsule_participants")
public class CapsuleParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @Column(name = "ready_to_close", nullable = false)
    private Boolean isReadyToClose;

    @Column(name = "added_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime addedAt;

    public CapsuleParticipant(Capsule capsule, User participant, Boolean isReadyToClose) {
        this.capsule = capsule;
        this.participant = participant;
        this.isReadyToClose = isReadyToClose;
        this.addedAt = LocalDateTime.now();
    }

    public static CapsuleParticipant ofCapsuleAndParticipantId(Long capsuleId, Long participantId, Boolean isReadyToClose) {
        Capsule capsule = new Capsule();
        capsule.setId(capsuleId);

        User participant = new User();
        participant.setId(participantId);

        return new CapsuleParticipant(capsule, participant, isReadyToClose);
    }

    public CapsuleParticipantResponseDto toResponseDto() {
        return new CapsuleParticipantResponseDto(participant.getId(), participant.getUsername(), isReadyToClose);
    }
}
