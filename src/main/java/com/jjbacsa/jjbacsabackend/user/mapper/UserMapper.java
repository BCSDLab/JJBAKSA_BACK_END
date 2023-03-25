package com.jjbacsa.jjbacsabackend.user.mapper;

import com.jjbacsa.jjbacsabackend.etc.enums.FollowedType;
import com.jjbacsa.jjbacsabackend.user.dto.request.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponseWithFollowedType;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toUserEntity(UserRequest userRequest);

    @Mapping(target = "userCountResponse", source = "userCount")
    UserResponse toUserResponse(UserEntity userEntity);

    @Mapping(target = "userCountResponse", source = "userEntity.userCount")
    UserResponseWithFollowedType toUserResponse(UserEntity userEntity, FollowedType followedType);
}
