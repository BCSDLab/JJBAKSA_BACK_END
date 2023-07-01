package com.jjbacsa.jjbacsabackend.post.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostCursorRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.mapper.PostMapper;
import com.jjbacsa.jjbacsabackend.post.repository.PostRepository;
import com.jjbacsa.jjbacsabackend.post.service.PostService;
import com.jjbacsa.jjbacsabackend.post_image.entity.PostImageEntity;
import com.jjbacsa.jjbacsabackend.post_image.service.InternalPostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final InternalPostImageService postImageService;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        return PostMapper.INSTANCE.toPostResponse(postEntity);
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) throws IOException {
        PostEntity postEntity = postRepository.save(createPostEntity(postRequest));
        return PostMapper.INSTANCE.toPostResponse(postEntity);
    }

    @Override
    public PostResponse modifyAdminPost(PostRequest postRequest, Long postId) throws IOException {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        postEntity.update(postRequest);
        if (postRequest.getPostImages() == null) {
            for (int i = postEntity.getPostImages().size() - 1; i >= 0; i--) {
                postImageService.delete(postEntity.getPostImages().get(i));
                postEntity.getPostImages().remove(i);
            }
        } else postImageService.modify(postRequest.getPostImages(), postEntity);
        return PostMapper.INSTANCE.toPostResponse(postEntity);

    }

    @Override
    public void deletePost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        postRepository.delete(postEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(PostCursorRequest postPageRequest) {
        return postRepository.findAllPosts(postPageRequest.getDateCursor(), postPageRequest.getIdCursor(), PageRequest.ofSize(postPageRequest.getSize()))
                .map(PostMapper.INSTANCE::toPostPageResponse);
    }

    private PostEntity createPostEntity(PostRequest postRequest) throws IOException {
        PostEntity postEntity = PostMapper.INSTANCE.toPostEntity(postRequest);
        if (postRequest.getPostImages() != null) {
            for (PostImageEntity image : postImageService.create(postRequest.getPostImages())) {
                postEntity.addPostImageEntity(image);
            }
        }
        return postEntity;
    }


}
