package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface CapsuleRepository extends JpaRepository<CapsuleEntity, Integer> {
}
