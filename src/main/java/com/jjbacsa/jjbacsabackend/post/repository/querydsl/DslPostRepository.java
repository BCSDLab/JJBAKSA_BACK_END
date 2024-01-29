package com.jjbacsa.jjbacsabackend.post.repository.querydsl;

import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslPostRepository {

    Page<PostEntity> findAllPosts(String cursor, Long idCursor, Pageable pageable);

}
