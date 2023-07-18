package com.jjbacsa.jjbacsabackend.post.repository;

import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.repository.querydsl.DslPostRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, DslPostRepository {

}