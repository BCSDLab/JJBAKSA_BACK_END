package com.jjbacsa.jjbacsabackend.follow.repository;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequestEntity, Long> {

    Optional<FollowRequestEntity> findByUserAndFollower(UserEntity user, UserEntity follower);

    List<FollowRequestEntity> findAllByUser(UserEntity user);
}