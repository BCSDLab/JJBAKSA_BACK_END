package com.jjbacsa.jjbacsabackend.post.repository.querydsl;

import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslPostRepository {
    Page<PostEntity> findAllFAQs(Pageable pageable);
    Page<PostEntity> findAllNotices(Pageable pageable);

}
