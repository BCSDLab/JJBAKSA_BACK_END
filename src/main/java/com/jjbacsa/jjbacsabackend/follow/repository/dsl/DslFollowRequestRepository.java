package com.jjbacsa.jjbacsabackend.follow.repository.dsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslFollowRequestRepository {

    Page<FollowRequestEntity> findAllByUser(UserEntity user, Pageable pageable);

    Page<FollowRequestEntity> findAllByFollower(UserEntity follower, Pageable pageable);

    Long deleteFollowRequestWithUser(UserEntity user);

    Boolean existsFollowReqeustsInLast24Hours(Long userId);
}
