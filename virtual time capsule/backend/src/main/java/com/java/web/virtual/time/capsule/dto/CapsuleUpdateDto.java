package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String openDateTime;

    @Valid // This annotation ensures that the nested GoalUpdatePartDto is also validated
    private GoalUpdatePartDto goal; // This can be null if goal is not being updated or created

    // NEW: Field for a user to mark themselves as ready to close
    private Boolean isReadyToClose; // true if the current user marks themselves ready
}
