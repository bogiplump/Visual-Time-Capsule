package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<CapsuleUser, Long> {

    CapsuleUser findUserByUsernameAndPassword(String username, String password);

    CapsuleUser findByUsername(String username);

    CapsuleUser findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    CapsuleUser save(CapsuleUser user);
}
