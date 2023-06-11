package com.jjbacsa.jjbacsabackend.post.mapper;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostPageResponse;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    
    PostResponse toPostResponse(PostEntity postEntity);

    PostPageResponse toPostPageResponse(PostEntity postEntity);

    PostEntity toPostEntity(PostRequest postRequest);
}
