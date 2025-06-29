package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.FriendshipStatus; // Ensure this enum is imported
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
    private Long requesterId; // ID of the requester user
    private String requesterUsername; // Username of the requester
    private Long responderId; // ID of the responder user
    private String responderUsername; // Username of the responder
    private FriendshipStatus status;
    private String lastUpdate; // ISO string
}
