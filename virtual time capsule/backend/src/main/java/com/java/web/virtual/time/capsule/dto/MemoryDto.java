package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.MemoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryDto {
    private Long id;
    private String description;
    private MemoryType memoryType;
    private String path; // Path to the stored content
    private String creationDate; // ISO string
    private Long creatorId; // Expecting creator's ID here
    private Long capsuleId; // Back-reference to capsule ID
}
