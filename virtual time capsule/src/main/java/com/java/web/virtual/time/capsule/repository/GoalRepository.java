package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleUser;
import com.java.web.virtual.time.capsule.model.GoalEntity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Integer> {
    List<GoalEntity> findByCreator(CapsuleUser creator);
}
