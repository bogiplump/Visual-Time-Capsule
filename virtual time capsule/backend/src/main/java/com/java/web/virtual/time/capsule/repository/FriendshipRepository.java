package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Friendship findByRequesterAndResponder(UserModel requester, UserModel responder);

    List<Friendship> findAllByRequesterAndResponder(UserModel requester, UserModel responder);

    List<Friendship> findByRequester(UserModel requester);

    List<Friendship> findAllByRequesterOrResponder(UserModel requester, UserModel responder);
}
