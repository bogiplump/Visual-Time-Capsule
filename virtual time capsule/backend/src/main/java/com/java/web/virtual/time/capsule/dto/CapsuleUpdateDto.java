package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleUpdateDto {
    @Size(min = 3, max = 100, message = "Capsule name must be between 3 and 100 characters")
    private String capsuleName;
    private String openDateTime;
    @Valid 
    private GoalUpdatePartDto goal;
    private Boolean isReadyToClose; 
}
