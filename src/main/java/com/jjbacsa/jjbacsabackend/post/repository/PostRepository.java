package com.jjbacsa.jjbacsabackend.post.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.repository.querydsl.DslPostRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, DslPostRepository {

    // Todo: 페이지네이션
    List<PostEntity> findAllByBoardType(BoardType boardType);
}