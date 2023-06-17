package com.jjbacsa.jjbacsabackend.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.annotation.WithMockCustomUser;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostCursorRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.serviceImpl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import javax.transaction.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    @Test
    @WithMockCustomUser(id = "4", role = UserType.ADMIN)
    void givenPostInfo_whenWritePost_thenCreatePost() throws Exception {
        String title = "title";
        String content = "content";
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",                // 파일 이름
                "image.jpg",            // 파일 이름과 확장자
                MediaType.IMAGE_JPEG_VALUE, // 이미지 파일의 MIME 타입
                loadImageBytes()        // 이미지 파일의 바이트 배열
        );
        PostRequest postRequest = createPostRequest(title, content, imageFile);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/post")
                        .param("title", postRequest.getTitle())
                        .param("content", postRequest.getContent())
                        .content(postRequest.getPostImages().get(0).getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                ).andDo(print())
                .andExpect(status().isCreated());
        then(postService).should().createPost(any(PostRequest.class));
    }

    @DisplayName("Post Id와 Post 수정 내용을 주면, Post를 수정한다.")
    @Test
    @WithMockCustomUser(id = "4", role = UserType.ADMIN)
    void givenPostInfo_whenModifyPost_thenModifyPost() throws Exception {
        String title = "new title";
        String content = "new content";
        Long postId = 1L;
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",                // 파일 이름
                "image.jpg",            // 파일 이름과 확장자
                MediaType.IMAGE_JPEG_VALUE, // 이미지 파일의 MIME 타입
                loadImageBytes()        // 이미지 파일의 바이트 배열
        );
        PostRequest postRequest = createPostRequest(title, content, imageFile);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/post/"+postId)
                        .param("title", postRequest.getTitle())
                        .param("content", postRequest.getContent())
                        .content(postRequest.getPostImages().get(0).getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        then(postService).should().modifyAdminPost(any(PostRequest.class), eq(postId));
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
    @Test
    void givenCursorRequest_whenGetPosts_thenPostsPage() throws Exception {
        PostCursorRequest pageRequest = createPageRequest();

        mockMvc.perform(get("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cursor", String.valueOf(pageRequest.getCursor()))
                        .param("size", String.valueOf(pageRequest.getSize()))
                        .content(objectMapper.writeValueAsBytes(pageRequest))
                ).andDo(print())
                .andExpect(status().isOk());

        then(postService).should().getPosts(refEq(pageRequest));
    }


    @DisplayName("Post Id로 Post를 삭제한다.")
    @Test
    @WithMockCustomUser(id = "4", role = UserType.ADMIN)
    void givenPostId_whenDeletePost_thenReturnNothing() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/admin/post/" + postId)
                ).andDo(print())
                .andExpect(status().isNoContent());
        then(postService).should().deletePost(postId);
    }

    private PostRequest createPostRequest(String title, String content) {
        return PostRequest.builder()
                .title(title)
                .content(content).build();
    }

    private PostRequest createPostRequest(String title, String content, MockMultipartFile image) {
        return PostRequest.builder()
                .title(title)
                .content(content)
                .postImages(List.of(image)).build();
    }

    private PostCursorRequest createPageRequest() {
        return PostCursorRequest.builder()
                .build();
    }

    private byte[] loadImageBytes() throws IOException {
        URL imageUrl = new URL("https://jjbaksa-stage-storage.s3.ap-northeast-2.amazonaws.com/review/b2362afd-7991-43f4-9c3f-b82213314765.png"); // 실제 이미지 파일의 URL
        try (InputStream inputStream = imageUrl.openStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return imageBytes;
        }
    }
}
