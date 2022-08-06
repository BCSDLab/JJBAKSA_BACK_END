package com.jjbacsa.jjbacsabackend.user.mapper;

import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toUserEntity(UserRequest userRequest);

    @Mapping(source = "OAuthType", target = "oAuthType")
    UserResponse toUserResponse(UserEntity userEntity);
}
