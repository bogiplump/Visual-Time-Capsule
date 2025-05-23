package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.entity.GoalEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Integer> {
}
