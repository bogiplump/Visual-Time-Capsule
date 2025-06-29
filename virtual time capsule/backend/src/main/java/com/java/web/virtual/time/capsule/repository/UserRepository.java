package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserModel findUserByUsernameAndPassword(String username, String password);

    UserModel findByUsername(String username);

    UserModel findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @NotNull UserModel save(UserModel user);

}
