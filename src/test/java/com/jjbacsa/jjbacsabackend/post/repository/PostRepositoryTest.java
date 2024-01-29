package com.jjbacsa.jjbacsabackend.post.repository;

import com.jjbacsa.jjbacsabackend.etc.config.BeanConfig;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(BeanConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void findAllByBoardType() {
        int size = 3;
        String title = "title";
        String content = "content";

        PostEntity noticePost = PostEntity.builder()
                .title(title)
                .content(content)
                .build();

        postRepository.save(noticePost);

        Page<PostEntity> noticePosts = postRepository.findAllPosts(null, null, PageRequest.ofSize(size));

        assertEquals(noticePosts.get().collect(Collectors.toList()).get(0).getContent(), content);
    }
}