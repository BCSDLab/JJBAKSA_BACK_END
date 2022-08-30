package com.jjbacsa.jjbacsabackend.follow.mapper;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface FollowMapper {

    FollowMapper INSTANCE = Mappers.getMapper(FollowMapper.class);

    FollowResponse toFollowResponse(FollowEntity follow);
}
