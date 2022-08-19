package com.jjbacsa.jjbacsabackend.follow.mapper;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface FollowRequestMapper {

    FollowRequestMapper INSTANCE = Mappers.getMapper(FollowRequestMapper.class);

    FollowRequestResponse toFollowRequestResponse(FollowRequestEntity followRequestEntity);
}
