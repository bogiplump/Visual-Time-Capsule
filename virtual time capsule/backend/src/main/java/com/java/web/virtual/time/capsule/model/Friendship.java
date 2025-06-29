package com.java.web.virtual.time.capsule.model;


import com.java.web.virtual.time.capsule.enums.FriendshipStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(optional = false)
    private UserModel requester;
    @ManyToOne(optional = false)
    private UserModel responder;

    private FriendshipStatus status;
    private LocalDate lastUpdate;

    public Friendship() {

    }
}
