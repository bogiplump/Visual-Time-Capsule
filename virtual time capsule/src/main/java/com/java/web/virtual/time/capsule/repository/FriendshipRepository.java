package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.CapsuleUser;
import com.java.web.virtual.time.capsule.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Friendship findByRequesterAndResponder(CapsuleUser requester, CapsuleUser responder);
}
