package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.UserModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    Set<Capsule> findByCreator_Id(Long createdById);

    Set<Capsule> findBySharedWithUsersContaining(Set<UserModel> sharedWithUsers);

    List<Capsule> findAllByIdIn(List<Long> capsuleIds);

}
