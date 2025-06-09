package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.memory.MemoryResponseDto;
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

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Builder
@Table(name = "memory_entries")
public class Memory {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MemoryType memoryType;

    @Column(name = "content_path")
    private String path;

    @Column(name = "created_at")
    private LocalDate creationDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "capsules_id")
    private Capsule capsule;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User creator;

    public Memory() {

    }

    public MemoryResponseDto toMemoryResponseDto() {
        return null; //TODO implement
    }
}
