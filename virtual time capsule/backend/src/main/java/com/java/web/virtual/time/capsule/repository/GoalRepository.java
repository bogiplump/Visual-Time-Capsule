package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Goal;

import java.util.List;

import com.java.web.virtual.time.capsule.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByCreator(UserModel creator);
}
