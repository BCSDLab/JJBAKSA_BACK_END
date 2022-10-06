package com.jjbacsa.jjbacsabackend.user.repository;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.querydsl.DslUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, DslUserRepository {

    Optional<UserEntity> findByAccount(String account);

    boolean existsByAccount(String account);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uc.reviewCount from UserCount uc " +
            "where uc.id = :userId")
    Integer getReviewCount(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uc.scrapCount from UserCount uc " +
            "where uc.id = :userId")
    Integer getScrapCount(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uc.friendCount from UserCount uc " +
            "where uc.id = :userId")
    Integer getFriendCount(@Param("userId") Long userId);
}