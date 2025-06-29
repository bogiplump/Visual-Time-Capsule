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
public class UserProfileDto {
    private Long id;
    private String username;
    private FriendshipStatus friendshipStatus;
    private Long associatedFriendshipId; 
    private Boolean isRequestFromCurrentUser; 
    private Boolean isRequestToCurrentUser;   
}

