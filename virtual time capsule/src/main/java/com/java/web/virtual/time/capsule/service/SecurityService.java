package com.java.web.virtual.time.capsule.service;

import org.springframework.stereotype.Service;

@Service
public interface SecurityService {
    /**
     * Retrieves the id of the current user.
     *
     * @return the unique identifier of the current user.
     */
    Long getCurrentUserId();
}
