package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapDirectoryRepository extends JpaRepository<ScrapDirectoryEntity, Long>, DslScrapDirectoryRepository {

    boolean existsByUserAndName(UserEntity user, String name);
}