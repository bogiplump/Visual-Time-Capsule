package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CapsuleParticipantRepository extends JpaRepository<CapsuleParticipant, Long> {
    void deleteByCapsuleIdAndParticipantId(Long capsuleId, Long participantId);

    Set<CapsuleParticipant> findByCapsule_Id(Long capsuleId);
}
