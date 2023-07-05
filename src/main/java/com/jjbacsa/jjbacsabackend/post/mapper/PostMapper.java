package com.jjbacsa.jjbacsabackend.post.mapper;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post_image.dto.response.PostImageResponse;
import com.jjbacsa.jjbacsabackend.post_image.entity.PostImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mappings({
            @Mapping(source = "image.originalName", target = "originalName"),
            @Mapping(source = "image.url", target = "imageUrl")
    })
    PostImageResponse toPostImageResponse(PostImageEntity postImageEntity);

    PostResponse toPostResponse(PostEntity postEntity);

    @Mapping(target = "postImages", ignore = true)
    @Mapping(target = "content", ignore = true)
    PostResponse toPostPageResponse(PostEntity postEntity);

    @Mapping(target = "postImages", ignore = true)
    PostEntity toPostEntity(PostRequest postRequest);
}
