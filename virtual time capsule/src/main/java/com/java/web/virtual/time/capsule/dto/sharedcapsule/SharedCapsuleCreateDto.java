package com.java.web.virtual.time.capsule.dto.sharedcapsule;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SharedCapsuleCreateDto {
    @NotNull
    private CapsuleCreateDto capsuleDto;
}
