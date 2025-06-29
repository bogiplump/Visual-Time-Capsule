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
    private Long associatedFriendshipId; // The ID of the friendship entity if one exists
    private Boolean isRequestFromCurrentUser; // True if current user sent a PENDING request to this user
    private Boolean isRequestToCurrentUser;   // True if this user sent a PENDING request to current user (current user needs to accept)
}

