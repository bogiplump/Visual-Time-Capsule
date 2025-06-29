package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleGroup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CapsuleGroupRepository extends JpaRepository<CapsuleGroup, Long> {
    @Query("SELECT DISTINCT g FROM CapsuleGroup g JOIN g.capsules c WHERE c.creator.id = :userId")
    List<CapsuleGroup> findDistinctByCapsulesCreatorId(@Param("userId") Long userId);
}
