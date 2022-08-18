package com.jjbacsa.jjbacsabackend.follow.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
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
    @Autowired
    private EntityManager em;

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
    @DisplayName("사용자& 팔로워 기준으로 검색")
    void findByUserAndFollower() {

        Optional<FollowEntity> dbFollow = followRepository.findByUserAndFollower(follow.getUser(), follow.getFollower());

        assertTrue(dbFollow.isPresent());
        assertEquals(follow, dbFollow.get());
    }

    @Test
    @DisplayName("커서 기반 페이징 테스트")
    void findAllByUserWithCursor() {

        for (int i = 1; i <= 5; ++i) {

            UserEntity follower = user1.toBuilder()
                    .account("user" + i)
                    .nickname("follower")
                    .build();
            follower = userRepository.save(follower);

            FollowEntity follow = FollowEntity.builder()
                    .user(user1)
                    .follower(follower)
                    .build();
            followRepository.save(follow);
        }

        em.clear();


        Page<FollowEntity> page1 = followRepository.findAllByUserWithCursor(user1, null, PageRequest.of(0, 10));
        Page<FollowEntity> page2 = followRepository.findAllByUserWithCursor(user1, page1.getContent().get(3).getCursor(), PageRequest.of(0, 10));

        assertEquals(7,page1.getContent().size());
        assertEquals(3,page2.getContent().size());
        assertEquals(page1.getContent().get(4),page2.getContent().get(0));
    }
}