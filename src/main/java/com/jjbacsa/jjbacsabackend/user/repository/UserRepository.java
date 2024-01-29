package com.jjbacsa.jjbacsabackend.user.repository;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.querydsl.DslUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, DslUserRepository {

    Optional<UserEntity> findByAccount(String account);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmailAndPasswordIsNotNull(String email);

    @Query(value =
            "select exists (select * from user u where u.account = :account " +
            "and (u.is_deleted = 0 " +
            "   or (u.is_deleted = 1 and u.updated_at > DATE_ADD(NOW(), INTERVAL - 1 DAY))))",
            nativeQuery = true)
    Integer existsByAccount(@Param("account") String account);

    boolean existsByEmailAndPasswordIsNotNull(String email);

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

    @Modifying
    @Query("update UserEntity u set u.lastLoggedAt = :loginTime " +
            "where u.id = :userId")
    Integer updateLastLoggedAt(@Param("userId") Long userId, @Param("loginTime") Date loginTime);
}