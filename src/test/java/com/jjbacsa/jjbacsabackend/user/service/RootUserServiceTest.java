package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.serviceImpl.RootUserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Slf4j
@DisplayName("RootUser 단위 테스트")
@SpringBootTest
@Sql(scripts = {"classpath:db/test/test_insert.sql"})
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class RootUserServiceTest {
    @Autowired
    private final RootUserServiceImpl rootUserService;
    @MockBean
    private final UserRepository userRepository;

    @DisplayName("user에게 admin 권한을 준다")
    @Test
    void givenUserAccount_whenAdminAuthority_thenAdminAuthority(){
        // Given
        String account = "account";

        // When
        when(userRepository.findByAccount(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        // Then
        Assertions.assertDoesNotThrow(() -> rootUserService.adminAuthority(account));
    }
    @DisplayName("없는 user에게 admin 권한을 주면, 에러 발생")
    @Test
    void givenNonExistUserAccount_whenAdminAuthority_thenThrowException(){
        // Given
        String account = "account";

        // When
        when(userRepository.findByAccount(any())).thenReturn(Optional.empty());

        // Then
        RequestInputException e = Assertions.assertThrows(RequestInputException.class, () -> rootUserService.adminAuthority(account));
        Assertions.assertEquals(ErrorMessage.USER_NOT_EXISTS_EXCEPTION.getErrorMessage(), e.getErrorMessage());
    }
    @DisplayName("user에게 normal 권한을 준다.")
    @Test
    void givenUserAccount_whenNormalAuthority_thenNormalAuthority(){
        // Given
        String account = "account";

        // When
        when(userRepository.findByAccount(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        // Then
        Assertions.assertDoesNotThrow(() -> rootUserService.normalAuthority(account));
    }
    @DisplayName("없는 user의 account를 주면, 에러 발생")
    @Test
    void givenNonExistUserAccount_whenNormalAuthority_thenThrowException(){
        // Given
        String account = "account";

        // When
        when(userRepository.findByAccount(any())).thenReturn(Optional.empty());

        // Then
        RequestInputException e = Assertions.assertThrows(RequestInputException.class, () -> rootUserService.normalAuthority(account));
        Assertions.assertEquals(ErrorMessage.USER_NOT_EXISTS_EXCEPTION.getErrorMessage(), e.getErrorMessage());
    }

}
