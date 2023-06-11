package com.jjbacsa.jjbacsabackend.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.annotation.WithMockCustomUser;
import com.jjbacsa.jjbacsabackend.etc.dto.CustomPageRequest;
import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostPageRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.serviceImpl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import javax.transaction.Transactional;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("컨트롤러 테스트 - Post")
@Sql(scripts = {"classpath:db/test/test_insert.sql"})
@RequiredArgsConstructor
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostServiceImpl postService;

    @DisplayName("Post를 작성하면, Post를 저장한다.")
    @MethodSource
    @ParameterizedTest(name="[BoardType] \"{0}\"")
    @WithMockCustomUser(id="4", role = UserType.ADMIN)
    void givenPostInfo_whenWritePost_thenCreatePost(String boardType) throws Exception {
        String title = "title";
        String content = "content";
        PostRequest postRequest = createPostRequest(title, content, boardType);

        mockMvc.perform(post("/admin/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(postRequest))
                ).andDo(print())
                .andExpect(status().isCreated());
        then(postService).should().createPost(any(PostRequest.class));
    }
    static Stream<Arguments> givenPostInfo_whenWritePost_thenCreatePost(){return getBoardType();}

    @DisplayName("Post Id와 Post 수정 내용을 주면, Post를 수정한다.")
    @Test
    @WithMockCustomUser(id="4", role = UserType.ADMIN)
    void givenPostInfo_whenModifyPost_thenModifyPost() throws Exception {
        String title = "new title";
        String content = "new content";
        Long postId = 1L;
        PostRequest postRequest = createPostRequest(title, content);

        mockMvc.perform(patch("/admin/post/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest))
                ).andDo(print())
                .andExpect(status().isOk());
        then(postService).should().modifyAdminPost(postRequest, postId);
    }


    @DisplayName("Post Id로 Post를 조회한다.")
    @Test
    void givenPostId_whenGetPost_thenReturnPost() throws Exception {
        Long postId = 1L;

        mockMvc.perform(get("/post/" + postId)
                ).andDo(print())
                .andExpect(status().isOk());
        then(postService).should().getPost(postId);
    }

    @DisplayName("Post 페이지를 조회한다.")
    @MethodSource
    @ParameterizedTest(name="[BoardType] \"{0}\"")
    void givenBoardType_whenGetPosts_thenPostsPage(String boardType) throws Exception {
        PostPageRequest pageRequest = createPageRequest(boardType);

        mockMvc.perform(get("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pageRequest))
                ).andDo(print())
                .andExpect(status().isOk());
        then(postService).should().getPosts(pageRequest);
    }
    static Stream<Arguments> givenBoardType_whenGetPosts_thenPostsPage(){return getBoardType();}


    @DisplayName("Post Id로 Post를 삭제한다.")
    @Test
    @WithMockCustomUser(id="4", role = UserType.ADMIN)
    void givenPostId_whenDeletePost_thenReturnNothing() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/admin/post/" + postId)
                ).andDo(print())
                .andExpect(status().isNoContent());
        then(postService).should().deletePost(postId);
    }
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
    private static Stream<Arguments> getBoardType(){
        return Stream.of(
                arguments(BoardType.NOTICE.getBoardType()),
                arguments(BoardType.POWER_NOTICE.getBoardType())
                );
    }
    private PostPageRequest createPageRequest(String boardType){
        return PostPageRequest.builder()
                .boardType(boardType)
                .build();
    }
}
