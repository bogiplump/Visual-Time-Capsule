package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserModel findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @NotNull UserModel save(@NotNull UserModel user);
}
