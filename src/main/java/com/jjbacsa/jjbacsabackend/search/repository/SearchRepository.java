package com.jjbacsa.jjbacsabackend.search.repository;

import com.jjbacsa.jjbacsabackend.search.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<SearchEntity, Long> {
    boolean existsByContent(String content);

    Optional<SearchEntity> findByContent(String Content);

    List<SearchEntity> findTop5ByContentContainingOrderByScoreDesc(String content);

}
