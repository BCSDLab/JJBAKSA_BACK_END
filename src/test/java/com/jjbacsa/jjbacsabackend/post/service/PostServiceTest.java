package com.jjbacsa.jjbacsabackend.post.service;

import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.repository.PostRepository;
import com.jjbacsa.jjbacsabackend.post.serviceImpl.PostServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RequiredArgsConstructor
@DisplayName("비즈니스 로직 - Post")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    /*
        TODO:
         Inquery(문의)에 대한 요구사항 파악 후 기능 추가
     */

    @InjectMocks
    private final PostServiceImpl postService;
    @Mock
    private final PostRepository postRepository;

    @DisplayName("공지, FAQ Post를 작성하면, Post를 저장한다.")
    @Test
    void givenPostInfo_whenWritePost_thenCreatePost(){
        //Given
        PostRequest post = createAdminPostRequest();
        given(postRepository.save(any(PostEntity.class))).willReturn(createPostEntity(post));


        // When
        sut.saveArticle(dto);

        // Then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
        then(articleRepository).should().save(any(Article.class));

        // When
        postService.createPost(post);

        // Then
        Assertions.assertDoesNotThrow(() -> postService.createPost(post));
        then(postRepository).sh
    }

    @DisplayName("공지, FAQ Post를 수정")
    @Test
    void givenPostInfo_whenUpdatePost_thenUpdatePost(){
        //Given
        PostRequest post = createAdminPostRequest();
        Long postId = 1L;
        PostEntity postEntity = createPostEntity(post);

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postRepository.saveAndFlush(any())).thenReturn(postEntity);

        // Then
        Assertions.assertDoesNotThrow(() -> postService.modifyAdminPost(post, postId));
    }

    @DisplayName("공지, FAQ Post를 수정시 Post가 존재하지 않는 경우")
    @Test
    void givenPostInfoByMissingId_whenUpdatePost_thenThrowException(){
        //Given
        PostRequest post = createAdminPostRequest();
        Long postId = 1L;

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Then
        RequestInputException e = Assertions.assertThrows(RequestInputException.class, () -> postService.modifyAdminPost(post,postId));
        Assertions.assertEquals(ErrorMessage.POST_NOT_EXISTS_EXCEPTION.getErrorMessage(), e.getErrorMessage());
    }

    @DisplayName("공지, FAQ Post를 삭제")
    @Test
    void givenPostId_whenDeletePost_thenDeletePost(){
        //Given
        PostRequest post = createAdminPostRequest();
        Long postId = 1L;
        PostEntity postEntity = createPostEntity(post);

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // Then
        Assertions.assertDoesNotThrow(() -> postService.deletePost(postId));
    }
    @DisplayName("공지, FAQ Post를 삭제시 Post가 존재하지 않는 경우")
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

    @DisplayName("공지에 대한 page와 size를 주면, 공지 글 페이지를 반환한다.")
    @Test
    void givenPageAndSize_whenGetNotices_thenReturnNoticesPage(){
        //Given
        Integer page = 0;
        Integer size = 3;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);

        // When
        when(postRepository.findAllNotices(pageRequest)).thenReturn(Page.empty());

        // Then
        Assertions.assertDoesNotThrow(() -> postService.getNotices(page, size));

    }



    private PostRequest createAdminPostRequest() {
        return new PostRequest("title", "content", BoardType.NOTICE.getBoardType());
    }

    private PostEntity createPostEntity(PostRequest postRequest){
       return PostEntity.builder()
               .title(postRequest.getTitle())
               .content(postRequest.getContent())
               .boardType(BoardType.valueOf(postRequest.getBoardType()))
               .build();
    }
}
