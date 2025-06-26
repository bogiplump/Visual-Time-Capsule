package com.java.web.virtual.time.capsule.dto.memory;


import com.java.web.virtual.time.capsule.enums.MemoryType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemoryCreateDto {
    private String name;
    private String description;
    private MemoryType type;
    private MultipartFile content;
}
