package com.jjbacsa.jjbacsabackend.post.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void findAllByBoardType() {

        PostEntity noticePost = PostEntity.builder()
                .title("title")
                .content("content")
                .boardType(BoardType.NOTICE)
                .build();

        PostEntity faqPost = noticePost.toBuilder().boardType(BoardType.FAQ).build();

        postRepository.save(noticePost);
        postRepository.save(faqPost);

        List<PostEntity> noticePosts = postRepository.findAllByBoardType(BoardType.NOTICE);
        List<PostEntity> faqPosts = postRepository.findAllByBoardType(BoardType.FAQ);

        assertEquals(noticePosts.get(0).getBoardType(), BoardType.NOTICE);
        assertEquals(faqPosts.get(0).getBoardType(), BoardType.FAQ);
    }
}