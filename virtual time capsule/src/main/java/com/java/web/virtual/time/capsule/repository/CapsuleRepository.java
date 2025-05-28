package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CapsuleRepository extends JpaRepository<CapsuleEntity, Long> {
    Set<CapsuleEntity> findByCreatedById(Long createdById);
}
