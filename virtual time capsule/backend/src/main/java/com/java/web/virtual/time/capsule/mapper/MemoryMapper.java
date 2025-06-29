package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.MemoryDto;
import com.java.web.virtual.time.capsule.model.Memory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemoryMapper {

    @Mapping(target = "creationDate", expression = "java(memory.getCreationDate() != null ? memory.getCreationDate().toString() : null)")
    @Mapping(target = "creatorId", source = "creator.id") // Map UserModel's ID
    @Mapping(target = "capsuleId", source = "capsule.id") // Map Capsule's ID
    MemoryDto toDto(Memory memory);
}
