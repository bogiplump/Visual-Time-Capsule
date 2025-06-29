package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.FriendshipStatus; 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDto {
    private Long id;
    private Long requesterId; 
    private String requesterUsername; 
    private Long responderId; 
    private String responderUsername; 
    private FriendshipStatus status;
    private String lastUpdate; 
}
