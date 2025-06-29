package com.java.web.virtual.time.capsule.repository;

import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Friendship findByRequesterAndResponder(UserModel requester, UserModel responder);


    List<Friendship> findAllByRequesterOrResponder(UserModel requester, UserModel responder);
}
