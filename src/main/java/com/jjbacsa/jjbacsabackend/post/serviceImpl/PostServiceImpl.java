package com.jjbacsa.jjbacsabackend.post.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.mapper.PostMapper;
import com.jjbacsa.jjbacsabackend.post.repository.PostRepository;
import com.jjbacsa.jjbacsabackend.post.service.PostService;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {
    /*
        TODO: Inquiry에 대한 처리를 명확히.
        문의했을 때 답변 방식, 문의 목록(나만?, 사용자 모두), 내가 문의한 것만 확인할 수 있는지
     */

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        return PostMapper.INSTANCE.toPostResponse(postEntity);
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        PostEntity postEntity = postRepository.save(createPostEntity(postRequest));
        return PostMapper.INSTANCE.toPostResponse(postEntity);
    }

    @Override
    public PostResponse modifyAdminPost(PostRequest postRequest, Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        modifyPostInfo(postEntity, postRequest);
        if(postRequest.getBoardType() != null) postEntity.setBoardType(BoardType.valueOf(postRequest.getBoardType()));

        return PostMapper.INSTANCE.toPostResponse(postEntity);

    }

    @Override
    public PostResponse modifyUserPost(PostRequest postRequest, Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        modifyPostInfo(postEntity, postRequest);

        return PostMapper.INSTANCE.toPostResponse(postEntity);
    }

    @Override
    public void deletePost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        postRepository.delete(postEntity);
    }

    @Override
    public Page<PostResponse> getFAQs(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return postRepository.findAllFAQs(pageRequest).map(PostMapper.INSTANCE::toPostResponse);
    }

    @Override
    public Page<PostResponse> getNotices(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return postRepository.findAllNotices(pageRequest).map(PostMapper.INSTANCE::toPostResponse);
    }

    private PostEntity createPostEntity(PostRequest postRequest) {
        return PostMapper.INSTANCE.toPostEntity(postRequest);
    }

    private void modifyPostInfo(PostEntity postEntity, PostRequest postRequest) {
        if(postRequest.getTitle() != null) postEntity.setTitle(postRequest.getTitle());
        if(postRequest.getContent() != null) postEntity.setContent(postRequest.getContent());
    }
}
