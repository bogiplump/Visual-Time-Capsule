package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
