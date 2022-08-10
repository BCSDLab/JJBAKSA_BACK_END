package com.jjbacsa.jjbacsabackend.user.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static UserEntity user;

    @BeforeAll
    static void init() {

        user = UserEntity.builder()
                .account("testuser")
                .password("password")
                .email("test@google.com")
                .nickname("testuser")
                .userType(UserType.NORMAL)
                .build();
    }

    @Test
    void findByAccount() {

        userRepository.save(user);
        Optional<UserEntity> dbUser = userRepository.findByAccount(user.getAccount());

        assertTrue(dbUser.isPresent());
        assertEquals(user, dbUser.get());
    }

    @Test
    void existsByAccount() {

        userRepository.save(user);

        assertTrue(userRepository.existsByAccount(user.getAccount()));
        assertFalse(userRepository.existsByAccount("testuser2"));
    }
}