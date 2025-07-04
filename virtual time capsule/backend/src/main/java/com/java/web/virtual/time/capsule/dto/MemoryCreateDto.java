package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.MemoryType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemoryCreateDto {
    private String description;
    private MemoryType type;
    private MultipartFile content;
}
