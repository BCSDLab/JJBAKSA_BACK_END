package com.jjbacsa.jjbacsabackend.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.annotation.WithMockCustomUser;
import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.serviceImpl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import javax.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("Post 통합 테스트")
@Sql(scripts = {"classpath:db/test/test_insert.sql"})
@RequiredArgsConstructor
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@Slf4j
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostServiceImpl postService;

    /*
            TODO:
             Inquery(문의)에 대한 요구사항 파악 후 기능 추가
     */

    @DisplayName("공지, FAQ Post를 작성하면, Post를 저장한다.")
    @Test
    @WithMockCustomUser
    void givenPostInfo_whenWritePost_thenCreatePost() throws Exception {
        String title = "title";
        String content = "content";
        BoardType boardType = BoardType.NOTICE;

        mockMvc.perform(post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(PostEntity.builder()
                        .title(title)
                        .content(content)
                        .boardType(boardType)
                        .build()))
                ).andDo(print())
                .andExpect(status().isOk());
    }
    @DisplayName("공지, FAQ Post를 수정하면, Post를 수정한다.")
    @Test
    @WithMockCustomUser
    void givenPostInfo_whenModifyPost_thenCreatePost() throws Exception {
        String title = "title";
        String content = "content";
        BoardType boardType = BoardType.NOTICE;

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostEntity.builder()
                                .title(title)
                                .content(content)
                                .boardType(boardType)
                                .build()))
                ).andDo(print())
                .andExpect(status().isOk());
    }
}
