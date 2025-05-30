package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.exception.user.UserNotFound;
import com.java.web.virtual.time.capsule.model.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    /**
     * Retrieves a UserEntity from the database by its unique identifier.
     *
     * @param id unique identifier of the user, must not be null.
     * @return the UserEntity object.
     *
     * @throws IllegalArgumentException if id is null.
     * @throws UserNotFound if user with this id does not exist.
     */
    UserEntity getUserById(Long id);
}
