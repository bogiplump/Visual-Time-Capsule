package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Memory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {

    boolean existsMemoryByCapsuleIsAndId(Capsule capsule, Long id);
}
