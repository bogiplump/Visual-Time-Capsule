package com.java.web.virtual.time.capsule.dto.capsule;

import com.java.web.virtual.time.capsule.dto.goal.GoalCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CapsuleCreateDto {
    @NotBlank(message = "Capsule name must not be blank")
    @Size(max = 20, message = "Capsule name must not exceed 20 characters")
    private String capsuleName;

    private GoalCreateDto goal;
}
