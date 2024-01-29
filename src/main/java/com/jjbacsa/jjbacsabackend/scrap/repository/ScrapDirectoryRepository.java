package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.repository.dsl.DslScrapDirectoryRepository;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface ScrapDirectoryRepository extends JpaRepository<ScrapDirectoryEntity, Long>, DslScrapDirectoryRepository {

    boolean existsByUserAndName(UserEntity user, String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sdc.scrapCount from ScrapDirectoryCount sdc " +
            "where sdc.id = :scrapDirectoryId")
    Integer getScrapCount(@Param("scrapDirectoryId") Long scrapDirectoryId);
}