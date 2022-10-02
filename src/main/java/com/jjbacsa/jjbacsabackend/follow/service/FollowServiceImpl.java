package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
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
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final InternalUserService userService;

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;

    @Override
    public FollowRequestResponse request(FollowRequest request) throws Exception {

        UserEntity user = userService.getLoginUser();
        UserEntity follower = userService.getUserByAccount(request.getUserAccount());

        checkValidFollowRequest(user, follower);
        FollowRequestEntity followRequest = saveFollowRequest(user, follower);

        return FollowRequestMapper.INSTANCE.toFollowRequestResponse(followRequest);
    }

    @Override
    public FollowResponse accept(Long requestId) throws Exception {

        UserEntity user = userService.getLoginUser();
        FollowRequestEntity followRequest = getFollowRequestById(requestId);

        checkRequestForMe(followRequest, user);
        deleteFollowRequest(followRequest);
        checkRequestSenderExists(followRequest);
        saveFollow(followRequest.getUser(), user);
        FollowEntity follow = saveFollow(user, followRequest.getUser());

        return FollowMapper.INSTANCE.toFollowResponse(follow);
    }

    @Override
    public void reject(Long requestId) throws Exception {

        UserEntity user = userService.getLoginUser();
        FollowRequestEntity followRequest = getFollowRequestById(requestId);

        checkRequestForMe(followRequest, user);
        deleteFollowRequest(followRequest);
    }

    @Override
    public void cancel(Long requestId) throws Exception {

        UserEntity user = userService.getLoginUser();
        FollowRequestEntity followRequest = getFollowRequestById(requestId);

        checkRequestByMe(followRequest, user);
        deleteFollowRequest(followRequest);
    }

    @Override
    public void delete(FollowRequest request) throws Exception {

        UserEntity user = userService.getLoginUser();
        UserEntity follower = userService.getUserByAccount(request.getUserAccount());

        checkUserIsFollower(user, follower);
        deleteFollow(user, follower);
        deleteFollow(follower, user);
    }

    @Override
    public Page<FollowRequestResponse> getSendRequests(Pageable pageable) throws Exception {

        UserEntity user = userService.getLoginUser();

        return followRequestRepository.findAllByUser(user, pageable).map(FollowRequestMapper.INSTANCE::toFollowRequestResponse);
    }

    @Override
    public Page<FollowRequestResponse> getReceiveRequests(Pageable pageable) throws Exception {

        UserEntity user = userService.getLoginUser();

        return followRequestRepository.findAllByFollower(user, pageable).map(FollowRequestMapper.INSTANCE::toFollowRequestResponse);
    }

    @Override
    public Page<UserResponse> getFollowers(String cursor, Pageable pageable) throws Exception {

        UserEntity user = userService.getLoginUser();

        return followRepository.findAllByUserWithCursor(user, cursor, pageable).map(follow -> UserMapper.INSTANCE.toUserResponse(follow.getFollower()));
    }

    private FollowRequestEntity getFollowRequestById(Long id) throws RequestInputException {

        return followRequestRepository.findById(id)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.FOLLOW_REQUEST_NOT_EXISTS_EXCEPTION));
    }

    private void checkValidFollowRequest(UserEntity user, UserEntity follower) throws RequestInputException {

        if (user.equals(follower))
            throw new RequestInputException(ErrorMessage.FOLLOW_REQUEST_MYSELF_EXCEPTION);

        if (followRequestRepository.existsByUserAndFollower(user, follower))
            throw new RequestInputException(ErrorMessage.FOLLOW_REQUEST_DUPLICATION_EXCEPTION);

        if (followRequestRepository.existsByUserAndFollower(follower, user))
            throw new RequestInputException(ErrorMessage.ALREADY_FOLLOW_REQUESTED_EXCEPTION);

        if (followRepository.existsByUserAndFollower(user, follower))
            throw new RequestInputException(ErrorMessage.ALREADY_FOLLOWED_EXCEPTION);
    }

    private void checkRequestForMe(FollowRequestEntity followRequest, UserEntity user) throws RequestInputException {

        if (!followRequest.getFollower().equals(user))
            throw new RequestInputException(ErrorMessage.FOLLOW_REQUEST_NOT_EXISTS_EXCEPTION);
    }

    private void checkRequestByMe(FollowRequestEntity followRequest, UserEntity user) {

        if (!followRequest.getUser().equals(user))
            throw new RequestInputException(ErrorMessage.FOLLOW_REQUEST_NOT_EXISTS_EXCEPTION);
    }

    private void checkRequestSenderExists(FollowRequestEntity followRequest) {

        if (followRequest.getUser() == null || followRequest.getUser().getIsDeleted() == 1)
            throw new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION);
    }

    private void checkUserIsFollower(UserEntity user, UserEntity follower) {

        if (!followRepository.existsByUserAndFollower(user, follower))
            throw new RequestInputException(ErrorMessage.NOT_FOLLOWED_EXCEPTION);
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
        userService.increaseFriendCount(user.getId());

        return followRepository.save(follow);
    }

    private void deleteFollowRequest(FollowRequestEntity followRequest) {

        followRequestRepository.delete(followRequest);
        followRequest.setIsDeleted(1);
    }

    private void deleteFollow(UserEntity user, UserEntity follower) {

        followRepository.findByUserAndFollower(user, follower)
                .ifPresentOrElse((FollowEntity follow) -> {

                    followRepository.delete(follow);
                    follow.setIsDeleted(1);
                    userService.decreaseFriendCount(user.getId());
                }, null);
    }
}
