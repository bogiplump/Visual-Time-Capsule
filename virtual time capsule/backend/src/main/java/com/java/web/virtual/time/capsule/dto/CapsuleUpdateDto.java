package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.annotation.ValidName;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CapsuleUpdateDto {


    @ValidName
    private String capsuleName;

    @NotNull
    @Future(message = "Open date must be in the future")
    private LocalDateTime openDate;

    @NotNull
    private GoalDto goal;
}
