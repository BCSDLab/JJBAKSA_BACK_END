package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.mapper.FollowMapper;
import com.jjbacsa.jjbacsabackend.follow.mapper.FollowRequestMapper;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRequestRepository;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final int followerPageSize = 20;
    private final int requestPageSize = 20;

    private final UserService userService;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;

    @Override
    public FollowRequestResponse request(FollowRequest request) throws Exception {

        UserEntity user = getLoginUser();
        UserEntity follower = getUserByAccount(request.getUserAccount());

        checkValidFollowRequest(user, follower);

        return FollowRequestMapper.INSTANCE.toFollowRequestResponse(saveFollowRequest(user, follower));
    }

    @Override
    public FollowResponse accept(Long requestId) throws Exception {

        UserEntity user = getLoginUser();
        FollowRequestEntity followRequest = getFollowRequestById(requestId);

        if (!followRequest.getFollower().equals(user))
            throw new RuntimeException("Request not exists.");

        followRequestRepository.delete(followRequest);
        followRequest.setIsDeleted(1);

        if (followRequest.getUser() == null || followRequest.getUser().getIsDeleted() == 1)
            throw new RuntimeException("User not exists.");

        saveFollow(followRequest.getUser(), user);

        return FollowMapper.INSTANCE.toFollowResponse(saveFollow(user, followRequest.getUser()));
    }

    @Override
    public void reject(Long requestId) throws Exception {

        UserEntity user = getLoginUser();
        FollowRequestEntity followRequest = getFollowRequestById(requestId);

        if (!followRequest.getFollower().equals(user))
            throw new RuntimeException("Request not exists.");

        followRequestRepository.delete(followRequest);
        followRequest.setIsDeleted(1);
    }

    @Override
    public void cancel(Long requestId) throws Exception {

        UserEntity user = getLoginUser();
        FollowRequestEntity followRequest = getFollowRequestById(requestId);

        if (!followRequest.getUser().equals(user))
            throw new RuntimeException("Request not exists.");

        followRequestRepository.delete(followRequest);
        followRequest.setIsDeleted(1);
    }

    @Override
    public void delete(FollowRequest request) throws Exception {

        UserEntity user = getLoginUser();
        UserEntity follower = getUserByAccount(request.getUserAccount());

        if (!followRepository.existsByUserAndFollower(user, follower))
            throw new RuntimeException("Not followed.");

        deleteFollow(user, follower);
        deleteFollow(follower, user);
    }

    @Override
    public Page<FollowRequestResponse> getSendRequests(int page) throws Exception {

        UserEntity user = getLoginUser();
        Pageable pageable = PageRequest.of(page, requestPageSize);

        return followRequestRepository.findAllByUser(user, pageable).map(FollowRequestMapper.INSTANCE::toFollowRequestResponse);
    }

    @Override
    public Page<FollowRequestResponse> getReceiveRequests(int page) throws Exception {

        UserEntity user = getLoginUser();
        Pageable pageable = PageRequest.of(page, requestPageSize);

        return followRequestRepository.findAllByFollower(user, pageable).map(FollowRequestMapper.INSTANCE::toFollowRequestResponse);
    }

    @Override
    public Page<UserResponse> getFollowers(String cursor) throws Exception {

        UserEntity user = getLoginUser();
        Pageable pageable = PageRequest.of(0, followerPageSize);

        return followRepository.findAllByUserWithCursor(user, cursor, pageable).map(follow -> UserMapper.INSTANCE.toUserResponse(follow.getFollower()));
    }

    private UserEntity getLoginUser() throws Exception {

        return userRepository.findById(userService.getLoginUser().getId())
                .orElseThrow(() -> new RuntimeException("User not logged in."));
    }

    private UserEntity getUserByAccount(String account) {

        return userRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    private FollowRequestEntity getFollowRequestById(Long id) {

        return followRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not exists."));
    }

    private void checkValidFollowRequest(UserEntity user, UserEntity follower) {

        if (user.equals(follower))
            throw new RuntimeException("Not request to yourself.");

        if (followRequestRepository.existsByUserAndFollower(user, follower))
            throw new RuntimeException("Request Duplicated.");

        if (followRequestRepository.existsByUserAndFollower(follower, user))
            throw new RuntimeException("Already exists request.");

        if (followRepository.existsByUserAndFollower(user, follower))
            throw new RuntimeException("Already followed.");
    }

    private FollowRequestEntity saveFollowRequest(UserEntity user, UserEntity follower) {

        FollowRequestEntity followRequest = FollowRequestEntity.builder()
                .user(user)
                .follower(follower)
                .build();

        return followRequestRepository.save(followRequest);
    }

    private FollowEntity saveFollow(UserEntity user, UserEntity follower) {

        FollowEntity follow = FollowEntity.builder()
                .user(user)
                .follower(follower)
                .build();
        user.increaseFriendCount();

        return followRepository.save(follow);
    }


    private void deleteFollow(UserEntity user, UserEntity follower) {

        followRepository.findByUserAndFollower(user, follower)
                .ifPresentOrElse((FollowEntity follow) -> {

                    followRepository.delete(follow);
                    follow.setIsDeleted(1);
                    user.decreaseFriendCount();
                }, null);
    }
}
