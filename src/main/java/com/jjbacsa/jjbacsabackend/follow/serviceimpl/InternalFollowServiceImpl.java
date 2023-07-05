package com.jjbacsa.jjbacsabackend.follow.serviceimpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRequestRepository;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalFollowServiceImpl implements InternalFollowService {

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final InternalUserService userService;

    @Override
    public FollowRequestEntity getFollowRequestById(Long id) throws RequestInputException {

        return followRequestRepository.findById(id)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.FOLLOW_REQUEST_NOT_EXISTS_EXCEPTION));
    }

    @Override
    public boolean existsByUserAndFollower(UserEntity user, UserEntity follower) {

        return followRepository.existsByUserAndFollower(user, follower);
    }

    @Override
    public Long deleteFollowWithUser(UserEntity user) {
        return followRepository.deleteFollowWithUser(user);
    }

    @Override
    public Long deleteFollowRequestWithUser(UserEntity user) {
        return followRequestRepository.deleteFollowRequestWithUser(user);
    }

    public List<UserEntity> getFollowers() throws Exception{
        UserEntity user = userService.getLoginUser();

        return followRepository.findAllByUser(user)
                .stream()
                .map(FollowEntity::getFollower)
                .collect(Collectors.toList());
    }
}
