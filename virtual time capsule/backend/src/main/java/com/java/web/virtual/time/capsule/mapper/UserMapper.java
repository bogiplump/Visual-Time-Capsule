package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.UserAuthResponse;
import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Named("userModelToUserProfileDto")
    UserProfileDto toUserProfileDto(UserModel userModel);

    // Maps UserModel to UserResponseDto
    @Mapping(target = "creationDate", expression = "java(userModel.getCreationDate() != null ? userModel.getCreationDate().toString() : null)")
    UserResponseDto toUserResponseDto(UserModel userModel);

    // Maps UserModel to UserAuthResponse
    // It needs to map the UserModel into the nested UserResponseDto field
    // and ignore token fields as they will be set externally.
    @Mapping(target = "accessToken", ignore = true) // Set by service layer
    @Mapping(target = "refreshToken", ignore = true) // Set by service layer
    @Mapping(target = "user", source = "userModel", qualifiedByName = "toUserResponseDtoForUserAuthResponse")
    UserAuthResponse toUserAuthResponse(UserModel userModel);

    // This helper method is specifically for mapping UserModel to the 'user' field
    // within UserAuthResponse, leveraging the existing toUserResponseDto logic.
    @Named("toUserResponseDtoForUserAuthResponse")
    default UserResponseDto toUserResponseDtoForUserAuthResponse(UserModel userModel) {
        return toUserResponseDto(userModel);
    }
}
