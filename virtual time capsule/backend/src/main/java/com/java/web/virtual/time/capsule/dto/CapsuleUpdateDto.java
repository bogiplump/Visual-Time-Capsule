package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.annotation.ValidName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for updating an existing Capsule.
 * Includes optional fields for capsule properties and a nested GoalUpdatePartDto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleUpdateDto {

    @Size(min = 3, max = 100, message = "Capsule name must be between 3 and 100 characters")
    private String capsuleName;

    // openDate can be null initially, but if set, should be a valid ISO string.
    // Frontend sends it as ISO string.
    private String openDateTime;

    // Use the new GoalUpdatePartDto for the nested goal update
    @Valid // This annotation ensures that the nested GoalUpdatePartDto is also validated
    private GoalUpdatePartDto goal; // This can be null if goal is not being updated or created
}

