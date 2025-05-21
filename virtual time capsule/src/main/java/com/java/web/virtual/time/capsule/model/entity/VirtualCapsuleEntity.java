package com.java.web.virtual.time.capsule.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;

import lombok.Data;

import org.springframework.data.annotation.Id;

@Data
@Entity
public class VirtualCapsuleEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "capsule_name")
    private String capsuleName;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "locked_on")
    private Date lockDate;
    @Column(name = "open_on")
    private Date openDate;
    @Column(name = "status")
    private CapsuleStatus capsuleStatus;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @Column(name = "created_by_id")
    private AccountEntity creatorId;

    // TODO: add memories, goals and participants when i get home
}
