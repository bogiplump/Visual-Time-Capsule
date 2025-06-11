package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.SharedCapsule;

import java.util.Optional;

public interface SharedCapsuleRepository {
    boolean existsByIdAndCreator_Id(Long id, Long creatorId);

    Optional<SharedCapsule> findById(Long sharedCapsuleId);

    SharedCapsule save(SharedCapsule sharedCapsule);

    void deleteById(Long sharedCapsuleId);
}
