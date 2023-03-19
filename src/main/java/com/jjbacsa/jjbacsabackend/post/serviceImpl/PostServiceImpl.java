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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {
    /*
            TODO:
             Inquery(문의)에 대한 요구사항 파악 후 기능 추가
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
        // BoardType은 수정이 불가능 함
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        postEntity.update(postRequest);
        return PostMapper.INSTANCE.toPostResponse(postEntity);

    }

    @Override
    public void deletePost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.POST_NOT_EXISTS_EXCEPTION));
        postRepository.delete(postEntity);
    }

    @Override
    public Page<PostResponse> getPosts(String boardType, Pageable pageable) {
        if(boardType.equals(BoardType.NOTICE.getBoardType()) || boardType.equals(BoardType.POWER_NOTICE.getBoardType())){
            return postRepository.findAllNotices(pageable).map(PostMapper.INSTANCE::toPostResponse);
        }
        else return postRepository.findAllInquiries(pageable).map(PostMapper.INSTANCE::toPostResponse);
    }

    private PostEntity createPostEntity(PostRequest postRequest) {
        return PostMapper.INSTANCE.toPostEntity(postRequest);
    }


}
