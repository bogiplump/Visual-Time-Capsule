package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleGroupEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CapsuleGroupRepository extends JpaRepository<CapsuleGroupEntity, Long> {
    @Query("SELECT DISTINCT g FROM CapsuleGroupEntity g JOIN g.capsules c WHERE c.creator.id = :userId")
    List<CapsuleGroupEntity> findDistinctByCapsulesCreatorId(@Param("userId") Long userId);
}
