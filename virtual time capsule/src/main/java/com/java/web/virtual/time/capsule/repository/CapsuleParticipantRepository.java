package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CapsuleParticipantRepository extends JpaRepository<CapsuleParticipant, Long> {
    void deleteByCapsuleIdAndParticipantId(Long capsuleId, Long participantId);

    void deleteByCapsuleId(Long capsuleId);

    Set<CapsuleParticipant> findByCapsuleId(Long capsuleId);

    CapsuleParticipant findByCapsuleIdAndParticipantId(Long capsuleId, Long participantId);

    boolean existsByCapsuleIdAndParticipantId(Long capsuleId, Long participantId);

    int countByCapsuleIdAndIsReadyToClose(Long capsuleId, Boolean isReadyToClose);

    int countByCapsuleId(Long capsuleId);
}
