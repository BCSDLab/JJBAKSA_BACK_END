package com.jjbacsa.jjbacsabackend.post.service;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.jjbacsa.jjbacsabackend.etc.dto.CustomPageRequest;
import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.repository.PostRepository;
import com.jjbacsa.jjbacsabackend.post.serviceImpl.PostServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;


@DisplayName("비즈니스 로직 - Post")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    /*
        TODO:
         Inquery(문의)에 대한 요구사항 파악 후 기능 추가
     */

    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private PostRepository postRepository;

    @DisplayName("Post를 작성하면, Post를 저장한다.")
    @MethodSource
    @ParameterizedTest(name="[BoardType] \"{0}\"")
    void givenPostInfo_whenWritePost_thenCreatePost(String boardType){
        //Given
        PostRequest post = createPostRequest("title", "content", boardType);
        given(postRepository.save(any(PostEntity.class))).willReturn(createPostEntity(post));

        // When
        PostResponse postResponse = postService.createPost(post);

        // Then
        assertThat(postResponse)
                .hasFieldOrPropertyWithValue("title", postResponse.getTitle())
                .hasFieldOrPropertyWithValue("content", postResponse.getContent())
                .hasFieldOrPropertyWithValue("boardType", postResponse.getBoardType());
        then(postRepository).should().save(any(PostEntity.class));
    }
    static Stream<Arguments> givenPostInfo_whenWritePost_thenCreatePost(){return getBoardType();}

    @DisplayName("Post를 수정한다.")
    @Test
    void givenPostInfo_whenUpdatePost_thenUpdatePost(){
        //Given
        PostEntity postEntity = createPostEntity(createPostRequest("title", "content", BoardType.NOTICE.getBoardType()));
        PostRequest modifyPost = createPostRequest("new title", null);
        Long postId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));

        // When
        PostResponse postResponse = postService.modifyAdminPost(modifyPost, postId);

        // Then
        assertThat(postEntity.getContent()).isEqualTo(postResponse.getContent());
        assertThat(postResponse)
                .hasFieldOrPropertyWithValue("title", postResponse.getTitle())
                .hasFieldOrPropertyWithValue("content", postResponse.getContent());
        then(postRepository).should().findById(postId);
    }
    @DisplayName("Post를 수정시 Post가 존재하지 않는 경우")
    @Test
    void givenPostInfoByMissingId_whenUpdatePost_thenThrowException(){
        //Given
        PostRequest modifyPost = createPostRequest("new title", "new content");
        Long postId = 1L;

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Then
        RequestInputException e = Assertions.assertThrows(RequestInputException.class, () -> postService.modifyAdminPost(modifyPost, postId));
        Assertions.assertEquals(ErrorMessage.POST_NOT_EXISTS_EXCEPTION.getErrorMessage(), e.getErrorMessage());
    }

    @DisplayName("PostId를 주면 Post를 삭제한다.")
    @Test
    void givenPostId_whenDeletePost_thenDeletePost(){
        //Given
        Long postId = 1L;
        PostEntity postEntity = createPostEntity(createPostRequest("title", "content", BoardType.NOTICE.getBoardType()));
        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));

        // When
        postService.deletePost(postId);

        // Then
        then(postRepository).should().findById(postId);
        then(postRepository).should().delete(postEntity);
    }

    @DisplayName("Post를 삭제시 Post가 존재하지 않는 경우")
    @Test
    void givenPostIdByMissingId_whenDeletePost_thenThrowException(){
        //Given
        Long postId = 1L;

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Then
        RequestInputException e = Assertions.assertThrows(RequestInputException.class, () -> postService.deletePost(postId));
        Assertions.assertEquals(ErrorMessage.POST_NOT_EXISTS_EXCEPTION.getErrorMessage(), e.getErrorMessage());
    }

    @DisplayName("BoardType과 Pageable 객체를 넘기면 공지글 페이지를 반환한다.")
    @Test
    void givenBoardTypeAndPageRequest_whenGetNotices_thenReturnPage(){
        //Given
        String boardType = BoardType.NOTICE.getBoardType();
        PageRequest pageRequest = createPageRequest();

        given(postRepository.findAllNotices(pageRequest)).willReturn(Page.empty());
        // When
        Page<PostResponse> postResponses = postService.getPosts(boardType, pageRequest);

        // Then
        assertThat(postResponses).isEmpty();
        then(postRepository).should().findAllNotices(pageRequest);
    }

    @DisplayName("BoardType과 Pageable 객체를 넘기면 FAQ, INQUIRY 페이지를 반환한다.")
    @MethodSource
    @ParameterizedTest(name = "[BoardType] \"{0}\"")
    void givenBoardTypeAndPageRequest_whenGetFAQsAndInquiries_thenReturnPage(String boardType){
        //Given
        PageRequest pageRequest = createPageRequest();
        given(postRepository.findAllNotices(pageRequest)).willReturn(Page.empty());
        // When
        Page<PostResponse> postResponses = postService.getPosts(boardType, pageRequest);

        // Then
        assertThat(postResponses).isEmpty();
        then(postRepository).should().findAllNotices(pageRequest);
    }
    static Stream<Arguments> givenBoardTypeAndPageRequest_whenGetFAQsAndInquiries_thenReturnPage(){return getFAQAndInqury();}


    private PostRequest createPostRequest(String title, String content, String boardType) {
        return PostRequest.builder()
                .title(title)
                .content(content).
                boardType(boardType).build();
    }
    private PostRequest createPostRequest(String title, String content) {
        return PostRequest.builder()
                .title(title)
                .content(content).build();
    }


    private PostEntity createPostEntity(PostRequest postRequest){
       return PostEntity.builder()
               .title(postRequest.getTitle())
               .content(postRequest.getContent())
               .boardType(BoardType.valueOf(postRequest.getBoardType()))
               .createdAt(new Date())
               .build();
    }

    private static Stream<Arguments> getBoardType(){
        return Stream.of(
                arguments(BoardType.NOTICE.getBoardType()),
                arguments(BoardType.POWER_NOTICE.getBoardType()),
                arguments(BoardType.FAQ.getBoardType())
        );
    }
    private static Stream<Arguments> getFAQAndInqury(){
        return Stream.of(
                arguments(BoardType.FAQ.getBoardType()),
                arguments(BoardType.INQUIRY.getBoardType())
        );
    }
    private PageRequest createPageRequest(){
        return CustomPageRequest.builder().build().of();
    }
}
