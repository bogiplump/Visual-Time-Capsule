package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SharedCapsuleCreateDto {
    @NotNull
    private CapsuleCreateDto capsuleDto;
}
