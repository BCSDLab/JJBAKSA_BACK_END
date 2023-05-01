package com.jjbacsa.jjbacsabackend.inquiry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.annotation.WithMockCustomUser;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.serviceImpl.InquiryServiceImpl;
import lombok.RequiredArgsConstructor;
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

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("컨트롤러 테스트 - Inquiry")
@Sql(scripts = {"classpath:db/test/test_insert.sql"})
@RequiredArgsConstructor
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class InquiryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InquiryServiceImpl inquiryService;

    @DisplayName("문의하기 글을 읽는다.")
    @Test
    @WithMockCustomUser(id = "1", role = UserType.NORMAL)
    void givenInquiryId_whenGetInquiry_thenGetInquiry() throws Exception {
        Long inquiryId = 1L;

        mockMvc.perform(get("/inquiry/" + inquiryId)
                ).andDo(print())
                .andExpect(status().isOk());
        then(inquiryService).should().getInquiry(inquiryId);
    }

    @DisplayName("문의글을 작성한다.")
    @Test
    @WithMockCustomUser(id = "1", role = UserType.NORMAL)
    void givenInquiryInfo_whenCreateInquiry_thenCreateInquiry() throws Exception {
        InquiryRequest request = createInquiryRequest(false);
        mockMvc.perform(post("/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                ).andDo(print())
                .andExpect(status().isCreated());
        then(inquiryService).should().create(refEq(request));
    }

    @DisplayName("자신이 작성한 문의글을 수정한다.")
    @Test
    @WithMockCustomUser(id = "1", role = UserType.NORMAL)
    void givenInquiryInfoAndInquiryId_whenModifyInquiry_thenModifyInquiry() throws Exception {
        Long inquiryId = 1L;
        String title = "new title";
        String content = "new content";
        InquiryRequest request = createInquiryRequest(title, content, false);

        mockMvc.perform(patch("/inquiry/" + inquiryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                ).andDo(print())
                .andExpect(status().isOk());
        then(inquiryService).should().modify(refEq(request), refEq(inquiryId));
    }

    @DisplayName("자신이 작성한 문의글을 삭제한다.")
    @Test
    @WithMockCustomUser(id = "1", role = UserType.NORMAL)
    void givenInquiryId_whenDeleteInquiry_thenDeleteInquiry() throws Exception {
        Long inquiryId = 1L;

        mockMvc.perform(delete("/inquiry/" + inquiryId)
                ).andDo(print())
                .andExpect(status().isNoContent());
        then(inquiryService).should().delete(inquiryId);
    }

    @DisplayName("관리자가 문의글에 답변한다.")
    @Test
    @WithMockCustomUser(id = "4", role = UserType.ADMIN)
    void givenInquiryId_whenAnswerInquiry_thenAddAnswerInquiry() throws Exception {
        Long inquiryId = 1L;
        AnswerRequest answer = createAnswerRequest();

        mockMvc.perform(patch("/admin/inquiry/" + inquiryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(answer))
                ).andDo(print())
                .andExpect(status().isOk());
        then(inquiryService).should().addAnswer(refEq(answer), refEq(inquiryId));
    }


    private InquiryRequest createInquiryRequest(Boolean isSecret) {
        return createInquiryRequest("title", "content", isSecret);
    }

    private InquiryRequest createInquiryRequest(String title, String content, Boolean isSecret) {
        return InquiryRequest.builder()
                .title(title)
                .content(content)
                .isSecret(isSecret)
                .build();
    }

    private AnswerRequest createAnswerRequest() {
        return AnswerRequest.builder()
                .answer("answer")
                .build();
    }

}
