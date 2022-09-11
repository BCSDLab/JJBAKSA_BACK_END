package com.jjbacsa.jjbacsabackend.follow.mapper;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface FollowRequestMapper {

    FollowRequestMapper INSTANCE = Mappers.getMapper(FollowRequestMapper.class);

    FollowRequestResponse toFollowRequestResponse(FollowRequestEntity followRequest);
}
