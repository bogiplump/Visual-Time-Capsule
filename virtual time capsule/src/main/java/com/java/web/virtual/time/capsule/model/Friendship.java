package com.java.web.virtual.time.capsule.model;


import com.java.web.virtual.time.capsule.enums.FriendShipStatus;
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
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User requester;
    @ManyToOne(optional = false)
    private User responder;

    @Setter
    @Getter
    private FriendShipStatus status;
    private LocalDate lastUpdate;

    public Friendship() {

    }
}
