package com.java.web.virtual.time.capsule.dto.capsule;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CapsulePreviewDto {
    private String capsuleName;
    private CapsuleStatus status;

    private LocalDateTime creationDate;
    private LocalDateTime lockDate;
    private LocalDateTime openDate;

    private String creatorName;
    private String goalContent;
}
