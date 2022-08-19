package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRequestRepository;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.CursorUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestConstructor;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
class FollowServiceTest {

    private final FollowService followService;
    @MockBean
    private final UserService userService;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;


    private UserEntity user1;
    private UserEntity user2;

    @BeforeEach
    void setup() {

        user1 = userRepository.save(getTestUser("testUser1"));
        user2 = userRepository.save(getTestUser("testUser2"));
    }

    @DisplayName("팔로우 요청")
    @Test
    void request() throws Exception {

        //사용자를 찾을 수 없을 경우
        assertThrows(RuntimeException.class, () ->
                followService.request(getFollowRequest(""))
        );

        //자기 자신인 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        assertThrows(RuntimeException.class, () ->
                followService.request(getFollowRequest(user2.getAccount()))
        );

        // 팔로우 요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));

        //중복 요청인 경우
        assertThrows(RuntimeException.class, () ->
                followService.request(getFollowRequest(user2.getAccount()))
        );

        //상대가 이미 나에게 팔로우 요청을 보낸 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        assertThrows(RuntimeException.class, () ->
                followService.request(getFollowRequest(user1.getAccount()))
        );

        //then
        FollowRequestEntity request = followRequestRepository.findByUserAndFollower(user1, user2).get();
        assertEquals(user1, request.getUser());
    }

    @DisplayName("팔로우 수락")
    @Test
    void accept() throws Exception {

        //요청이 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.accept(0L)
        );

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));
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

    @DisplayName("탈퇴한 유저가 보낸 팔로우 요청 수락")
    @Test
    void acceptWithdrawalUser() throws Exception {

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));
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

    @DisplayName("이미 팔로우된 사용자에게 팔로우 요청")
    @Test
    void requestAlreadyFollowed() throws Exception {

        //팔로우 요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));

        //팔로우 승인
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();
        followService.accept(followRequestEntity.getId());

        //이미 팔로우된 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        assertThrows(RuntimeException.class, () ->
                followService.request(getFollowRequest(user2.getAccount()))
        );
    }

    @DisplayName("팔로우 거절")
    @Test
    void reject() throws Exception {

        //요청이 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.reject(0L)
        );

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));
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

    @DisplayName("팔로우 요청 취소")
    @Test
    void cancel() throws Exception {

        //요청이 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.cancel(0L)
        );

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));
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

    @DisplayName("팔로우 해제")
    @Test
    void delete() throws Exception {

        //팔로우가 아닌 경우
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        assertThrows(RuntimeException.class, () ->
                followService.delete(getFollowRequest(user2.getAccount()))
        );

        //사용자를 찾을 수 없는 경우
        assertThrows(RuntimeException.class, () ->
                followService.request(getFollowRequest(""))
        );

        //요청
        followService.request(getFollowRequest(user2.getAccount()));
        FollowRequestEntity followRequestEntity = followRequestRepository.findByUserAndFollower(user1, user2).get();

        //수락
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        followService.accept(followRequestEntity.getId());
        FollowEntity follow1 = followRepository.findByUserAndFollower(user1, user2).get();
        FollowEntity follow2 = followRepository.findByUserAndFollower(user2, user1).get();

        //제거
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.delete(getFollowRequest(user2.getAccount()));

        //then
        assertEquals(1, follow1.getIsDeleted());
        assertEquals(1, follow2.getIsDeleted());
    }

    @DisplayName("보낸 팔로우 요청 목록 조회")
    @Test
    void getSendRequests() throws Exception {


        //유저3 생성
        UserEntity user3 = userRepository.save(getTestUser("testUser3"));

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));
        followService.request(getFollowRequest(user3.getAccount()));

        //조회
        Page<FollowRequestResponse> requests = followService.getSendRequests(0);
        assertEquals(user2.getId(), requests.getContent().get(0).getFollower().getId());
        assertEquals(user3.getId(), requests.getContent().get(1).getFollower().getId());
    }

    @DisplayName("받은 팔로우 요청 목록 조회")
    @Test
    void getReceiveRequests() throws Exception {

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));

        //조회
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        Page<FollowRequestResponse> requests = followService.getReceiveRequests(0);
        assertEquals(user1.getId(), requests.getContent().get(0).getUser().getId());
    }

    @DisplayName("팔로워 목록 조회")
    @Test
    void getFollowers() throws Exception {

        //유저3 생성
        UserEntity user3 = userRepository.save(getTestUser("testUser3"));

        //요청
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        followService.request(getFollowRequest(user2.getAccount()));
        followService.request(getFollowRequest(user3.getAccount()));
        FollowRequestEntity followRequest1 = followRequestRepository.findByUserAndFollower(user1, user2).get();
        FollowRequestEntity followRequest2 = followRequestRepository.findByUserAndFollower(user1, user3).get();

        //수락
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user2));
        followService.accept(followRequest1.getId());
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user3));
        followService.accept(followRequest2.getId());

        //조회
        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user1));
        Page<UserResponse> followers1 = followService.getFollowers(null);
        Page<UserResponse> followers2 = followService.getFollowers(CursorUtil.getFollowerCursor(user2));

        //then
        assertEquals(2, followers1.getContent().size());
        assertEquals(user2.getId(), followers1.getContent().get(0).getId());
        assertEquals(followers2.getContent().get(0).getId(), followers1.getContent().get(1).getId());
    }


    private UserEntity getTestUser(String account) {

        return UserEntity.builder()
                .account(account)
                .password("password")
                .email("test2@google.com")
                .nickname(account)
                .userType(UserType.NORMAL)
                .build();
    }

    private FollowRequest getFollowRequest(String account) {
        return FollowRequest.builder()
                .userAccount(account)
                .build();
    }
}