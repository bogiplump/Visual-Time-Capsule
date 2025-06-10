package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.FriendShipStatus;
import lombok.Data;

@Data
public class FriendshipDto {
    private Long receiverId;
    private FriendShipStatus status;
}
