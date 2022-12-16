package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.annotation.WithMockCustomUser;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.service.RootUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import javax.transaction.Transactional;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@DisplayName("Root User 통합 테스트")
@Sql(scripts = {"classpath:db/test/test_insert.sql"})
@RequiredArgsConstructor
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@Slf4j
public class RootUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RootUserService rootUserService;

    @DisplayName("사용자의 account를 주면, Admin권한을 준다.")
    @Test
    @WithMockCustomUser
    void givenAccount_whenAdminAuthority_thenAdminAuthority() throws Exception {
        mockMvc.perform(post("/root/admin?account=dpwns1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @DisplayName("없는 사용자 account를 주면, 에러를 반환한다.")
    @Test
    @WithMockCustomUser
    void givenNonExistAccount_whenAdminAuthority_thenThrowException() throws Exception {

        doThrow(new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION)).when(rootUserService).adminAuthority(eq("account"));

        mockMvc.perform(post("/root/admin?account=account"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @DisplayName("사용자의 account를 주면, Normal권한을 준다.")
    @Test
    @WithMockCustomUser
    void givenUserAccount_whenNormalAuthority_thenNormalAuthority() throws Exception {
        mockMvc.perform(post("/root/normal?account=dpwns1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @DisplayName("없는 사용자 account를 주면, 에러를 반환한다.")
    @Test
    @WithMockCustomUser
    void givenNonExistAccount_whenNormalAuthority_thenThrowException() throws Exception {

        doThrow(new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION)).when(rootUserService).normalAuthority(eq("account"));

        mockMvc.perform(post("/root/normal?account=account"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
