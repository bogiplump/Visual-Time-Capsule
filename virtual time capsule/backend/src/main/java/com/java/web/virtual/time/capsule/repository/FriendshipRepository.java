package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Friendship findByRequesterAndResponder(User requester, User responder);
}
