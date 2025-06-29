package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.model.Friendship; // Ensure this points to your model package
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requesterUsername", source = "requester.username")
    @Mapping(target = "responderId", source = "responder.id")
    @Mapping(target = "responderUsername", source = "responder.username")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "lastUpdate", expression = "java(friendship.getLastUpdate() != null ? friendship.getLastUpdate().toString() : null)")
    FriendshipDto toDto(Friendship friendship);
}
