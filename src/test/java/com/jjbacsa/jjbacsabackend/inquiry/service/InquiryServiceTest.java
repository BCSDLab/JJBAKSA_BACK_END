package com.jjbacsa.jjbacsabackend.inquiry.service;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry.repository.InquiryRepository;
import com.jjbacsa.jjbacsabackend.inquiry.serviceImpl.InquiryServiceImpl;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - Inquiry")
@ExtendWith(MockitoExtension.class)
public class InquiryServiceTest {

    @InjectMocks
    private InquiryServiceImpl inquiryService;

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private InternalUserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;


    @DisplayName("Inquiry를 비밀번호 없이 작성한다.")
    @Test
    void givenInquiryInfo_whenWriteInquiryWithoutSecret_ThenCreateInquiry() throws Exception {
        // Given
        InquiryRequest inquiryRequest = createInquiryRequest(null);
        given(inquiryRepository.save(any(InquiryEntity.class))).willReturn(createInquiryEntity(inquiryRequest));
        given(userService.getLoginUser()).willReturn(createUserEntity());

        // When
        inquiryService.create(inquiryRequest);

        // Then
        then(userService).should().getLoginUser();
        then(inquiryRepository).should().save(any(InquiryEntity.class));
    }

    @DisplayName("Inquiry를 작성 시 비밀글로 작성한다.")
    @Test
    void givenInquiryInfo_whenWriteInquiry_ThenCreateInquiry() throws Exception {
        // Given
        InquiryRequest inquiryRequest = createInquiryRequest("secret");
        given(inquiryRepository.save(any(InquiryEntity.class))).willReturn(createInquiryEntity(inquiryRequest));
        given(userService.getLoginUser()).willReturn(createUserEntity());
        given(passwordEncoder.encode(inquiryRequest.getSecret())).willReturn("");
        // When
        inquiryService.create(inquiryRequest);

        // Then
        then(userService).should().getLoginUser();
        then(inquiryRepository).should().save(any(InquiryEntity.class));
    }

    @DisplayName("자신이 작성한 Inquiry를 수정한다.")
    @Test
    void givenInquiryInfo_whenModifyInquiry_ThenModifiesInquiry() throws Exception {
        // Given
        InquiryEntity inquiryEntity = createInquiryEntity(createInquiryRequest(null));
        String title = "new title";
        String content = "new content";
        InquiryRequest inquiryRequest = createInquiryRequest(title, content, null);

        given(inquiryRepository.findById(inquiryEntity.getId())).willReturn(Optional.of(inquiryEntity));
        given(userService.getLoginUser()).willReturn(createUserEntity());

        // When
        inquiryService.modify(inquiryRequest, inquiryEntity.getId());

        // Then
        then(userService).should().getLoginUser();
        then(inquiryRepository).should().findById(inquiryEntity.getId());
    }

    @DisplayName("자신이 작성한 Inquiry를 삭제한다.")
    @Test
    void givenInquiryId_whenDeleteInquiry_ThenDeletesInquiry() throws Exception {
        // Given
        Long inquiryId = 1L;

        given(inquiryRepository.findById(inquiryId)).willReturn(Optional.of(createInquiryEntity(createInquiryRequest(null))));
        given(userService.getLoginUser()).willReturn(createUserEntity());
        willDoNothing().given(inquiryRepository).deleteById(inquiryId);

        // When
        inquiryService.delete(inquiryId);

        // Then
        then(inquiryRepository).should().findById(inquiryId);
        then(userService).should().getLoginUser();
        then(inquiryRepository).should().deleteById(inquiryId);
    }

    @DisplayName("Inquiry에 답변한다.")
    @Test
    void givenAnswerAndInquiryId_whenAnswerInquiry_ThenAnswersInquiry() throws Exception {
        // Given
        AnswerRequest answer = createAnswerRequest();
        Long inquiryId = 1L;
        given(inquiryRepository.findById(inquiryId)).willReturn(Optional.of(createInquiryEntity(createInquiryRequest(null))));

        // When
        inquiryService.addAnswer(answer, inquiryId);

        // Then
        then(inquiryRepository).should().findById(inquiryId);
    }

    private InquiryRequest createInquiryRequest(String secret){
        return createInquiryRequest("title", "content", secret);
    }

    private InquiryRequest createInquiryRequest(String title, String content, String secret){
        return InquiryRequest.builder()
                .title(title)
                .content(content)
                .secret(secret)
                .build();
    }
    private InquiryEntity createInquiryEntity(InquiryRequest inquiryRequest){
        return InquiryEntity.builder()
                .id(1L)
                .writer(createUserEntity())
                .title(inquiryRequest.getTitle())
                .content(inquiryRequest.getContent())
                .secret(inquiryRequest.getSecret())
                .isSecreted(inquiryRequest.getSecret() == null ? 0 : 1)
                .createdAt(new Date())
                .build();
    }

    private UserEntity createUserEntity(){
        return createUserEntity(1L);
    }

    private UserEntity createUserEntity(Long userId){
        return UserEntity.builder()
                .id(userId)
                .account("dpwns")
                .email("dpwns@naver.com")
                .nickname("test")
                .password("password")
                .userType(UserType.NORMAL)
                .build();
    }

    private AnswerRequest createAnswerRequest(){
        return AnswerRequest.builder()
                .answer("answer")
                .build();
    }

}
