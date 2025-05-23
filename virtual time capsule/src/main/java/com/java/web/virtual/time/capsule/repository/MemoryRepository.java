package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRepository extends JpaRepository<MemoryEntity, Integer> {
}
