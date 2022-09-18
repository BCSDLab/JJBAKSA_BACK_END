package com.jjbacsa.jjbacsabackend.user.mapper;

import com.jjbacsa.jjbacsabackend.user.dto.UserCountResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserCount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface UserCountMapper {
    UserCountMapper INSTANCE = Mappers.getMapper(UserCountMapper.class);

    UserCountResponse toUserCountResponse(UserCount userCount);
}
