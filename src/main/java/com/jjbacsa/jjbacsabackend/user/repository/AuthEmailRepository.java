package com.jjbacsa.jjbacsabackend.user.repository;

import com.jjbacsa.jjbacsabackend.user.entity.AuthEmailEntity;
import com.jjbacsa.jjbacsabackend.user.repository.querydsl.DslUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface AuthEmailRepository extends JpaRepository<AuthEmailEntity, Long>, DslUserRepository {

    @Query("SELECT COUNT(a.id) FROM AuthEmailEntity a WHERE a.createdAt BETWEEN :start AND current_timestamp AND a.user.id = :userId")
    Long getEmailCount(@Param("start") Timestamp start,
                       @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE AuthEmailEntity a SET a.isDeleted = 1 WHERE a.user.id = :userId")
    void deletePastEmail(@Param("userId") Long userId);

    Optional<AuthEmailEntity> findAuthEmailEntityByUserIdAndIsDeleted(Long userId, int isDeleted);

}
