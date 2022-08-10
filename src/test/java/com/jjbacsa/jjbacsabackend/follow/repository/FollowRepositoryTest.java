package com.jjbacsa.jjbacsabackend.follow.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;

    private static UserEntity user1;
    private static UserEntity user2;
    private static UserEntity user3;
    private static FollowEntity follow;

    @BeforeAll
    static void init() {

        user1 = UserEntity.builder()
                .account("testuser")
                .password("password")
                .email("test@google.com")
                .nickname("testuser")
                .userType(UserType.NORMAL)
                .build();

        user2 = user1.toBuilder().account("testuser2").build();

        user3 = user1.toBuilder().account("testuser3").build();
    }

    @BeforeEach
    void eachInit() {

        UserEntity dbUser1 = userRepository.save(user1);
        UserEntity dbUser2 = userRepository.save(user2);
        UserEntity dbUser3 = userRepository.save(user3);

        follow = FollowEntity.builder()
                .user(dbUser1)
                .follower(dbUser2)
                .build();

        FollowEntity follow2 = follow.toBuilder().follower(dbUser3).build();

        FollowEntity followInv = FollowEntity.builder()
                .user(dbUser2)
                .follower(dbUser1)
                .build();

        FollowEntity followInv2 = followInv.toBuilder().user(dbUser3).build();

        followRepository.save(follow);
        followRepository.save(followInv);
        followRepository.save(follow2);
        followRepository.save(followInv2);
    }

    @Test
    void findByUserAndFollower() {

        Optional<FollowEntity> dbFollow = followRepository.findByUserAndFollower(follow.getUser(), follow.getFollower());

        assertTrue(dbFollow.isPresent());
        assertEquals(follow, dbFollow.get());
    }

    @Test
    void findAllByUser() {

        List<FollowEntity> follows = followRepository.findAllByUser(follow.getUser());
        assertEquals(follows.size(), 2);
    }
}