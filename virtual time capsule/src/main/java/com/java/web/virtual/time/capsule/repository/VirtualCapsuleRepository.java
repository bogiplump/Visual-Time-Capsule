package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.entity.VirtualCapsuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualCapsuleRepository extends JpaRepository<VirtualCapsuleEntity, Integer> {
}
