package com.jjbacsa.jjbacsabackend.follow.repository.dsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslFollowRepository {

    Page<FollowEntity> findAllByUserWithCursor(UserEntity user, String cursor, Pageable pageable);

    Long deleteFollowWithUser(UserEntity user);

    Page<FollowEntity> findRecentlyActiveFollowersByUserWithCursor(UserEntity user, Long cursor, Pageable pageable);
}
