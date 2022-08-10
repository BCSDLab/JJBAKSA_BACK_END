package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRequestRepository;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    private FollowService followService;
    @MockBean
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FollowRequestRepository followRequestRepository;

    private static UserRequest userRequest1;
    private static UserRequest userRequest2;

    private UserEntity user1;
    private UserEntity user2;
    private FollowRequest followRequest;


    @BeforeAll
    static void init() {

        userRequest1 = UserRequest.builder()
                .account("testuser1")
                .password("password")
                .email("test1@google.com")
                .nickname("testuser1")
                .build();

        userRequest2 = UserRequest.builder()
                .account("testuser2")
                .password("password")
                .email("test2@google.com")
                .nickname("testuser2")
                .build();
    }

    @BeforeEach
    void setup() {

        user1 = UserMapper.INSTANCE.toUserEntity(userRequest1).toBuilder()
                .userType(UserType.NORMAL)
                .build();

        user2 = UserMapper.INSTANCE.toUserEntity(userRequest2).toBuilder()
                .userType(UserType.NORMAL)
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        followRequest = FollowRequest.builder()
                .userAccount(user2.getAccount())
                .build();
    }

    @Test
    void request() throws Exception {

        //사용자를 찾을 수 없을 경우
        FollowRequest followRequest2 = FollowRequest.builder()
                .userAccount("")
                .build();
        assertThrows(RuntimeException.class, () ->
                followService.request(followRequest2)
        );

        //자기 자신인 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        assertThrows(RuntimeException.class, () ->
                followService.request(followRequest)
        );

        // 팔로우 요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(followRequest);

        //중복 요청인 경우
        assertThrows(RuntimeException.class, () ->
                followService.request(followRequest)
        );

        //상대가 이미 나에게 팔로우 요청을 보낸 경우
        FollowRequest followRequest3 = FollowRequest.builder()
                .userAccount(user1.getAccount())
                .build();
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        assertThrows(RuntimeException.class, () ->
                followService.request(followRequest3)
        );

        //then
        FollowRequestEntity request = followRequestRepository.findByUserAndFollower(user1, user2).get();
        assertEquals(user1, request.getUser());
    }

    @Test
    void accept() throws Exception {

        //요청이 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.accept(0L)
        );

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(followRequest);
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();

        //나에게 온 요청이 아닌 경우
        assertThrows(RuntimeException.class, () ->
                followService.accept(followRequestEntity.getId())
        );

        //수락
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        followService.accept(followRequestEntity.getId());
        FollowEntity follow1 = followRepository.findByUserAndFollower(user1, user2).get();
        FollowEntity follow2 = followRepository.findByUserAndFollower(user2, user1).get();

        //then
        assertEquals(1, followRequestEntity.getIsDeleted());
        assertEquals(user2, follow1.getFollower());
        assertEquals(user1, follow2.getFollower());
    }

    @Test
    void acceptWithdrawalUser() throws Exception {

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(followRequest);
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();

        //탈퇴
        userRepository.delete(user1);
        user1.setIsDeleted(1);

        //탈퇴한 회원인 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        assertThrows(RuntimeException.class, () ->
                followService.accept(followRequestEntity.getId())
        );
        assertEquals(1, followRequestEntity.getIsDeleted());
    }

    @Test
    void requestAlreadyFollowed() throws Exception {

        //팔로우 요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(followRequest);

        //팔로우 승인
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();
        followService.accept(followRequestEntity.getId());

        //이미 팔로우된 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        assertThrows(RuntimeException.class, () ->
                followService.request(followRequest)
        );
    }

    @Test
    void reject() throws Exception {

        //요청이 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.reject(0L)
        );

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(followRequest);
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();

        //나에게 온 요청이 아닌 경우
        assertThrows(RuntimeException.class, () ->
                followService.reject(followRequestEntity.getId())
        );

        //거절
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        followService.reject(followRequestEntity.getId());

        //then
        assertEquals(1, followRequestEntity.getIsDeleted());
        assertTrue(followRepository.findByUserAndFollower(user1, user2).isEmpty());
    }

    @Test
    void cancel() throws Exception {

        //요청이 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.cancel(0L)
        );

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(followRequest);
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();

        //내가 보낸 요청이 아닌 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        assertThrows(RuntimeException.class, () ->
                followService.cancel(followRequestEntity.getId())
        );

        //취소
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.cancel(followRequestEntity.getId());

        //then
        assertEquals(1, followRequestEntity.getIsDeleted());
    }

    @Test
    void delete() throws Exception {

        //팔로우가 아닌 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        assertThrows(RuntimeException.class, () ->
                followService.delete(followRequest)
        );

        //사용자를 찾을 수 없는 경우
        FollowRequest followRequest2 = FollowRequest.builder()
                .userAccount("")
                .build();
        assertThrows(RuntimeException.class, () ->
                followService.request(followRequest2)
        );

        //요청
        followService.request(followRequest);
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();

        //수락
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        followService.accept(followRequestEntity.getId());
        FollowEntity follow1 = followRepository.findByUserAndFollower(user1, user2).get();
        FollowEntity follow2 = followRepository.findByUserAndFollower(user2, user1).get();

        //제거
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.delete(followRequest);

        //then
        assertEquals(1, follow1.getIsDeleted());
        assertEquals(1, follow2.getIsDeleted());
    }
}