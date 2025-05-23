package com.java.web.virtual.time.capsule.model.entity;

import com.java.web.virtual.time.capsule.enums.MemoryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "memory_entries")
public class MemoryEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MemoryType memoryType;

    @Column(name = "content_path")
    private String memoryLocation;

    @Column(name = "content_if_text")
    private String textContent;

    @Column(name = "created_at")
    private Date creationDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "capsules_id")
    private CapsuleEntity capsule;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserEntity creator;
}
