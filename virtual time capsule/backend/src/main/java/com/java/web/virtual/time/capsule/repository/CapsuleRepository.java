package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Capsule;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    Set<Capsule> findByCreator_Id(Long createdById);

    boolean existsByIdAndCreator_Id(Long id, Long createdById);

}
