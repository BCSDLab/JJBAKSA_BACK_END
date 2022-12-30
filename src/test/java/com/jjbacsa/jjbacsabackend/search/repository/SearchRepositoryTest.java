package com.jjbacsa.jjbacsabackend.search.repository;

import com.jjbacsa.jjbacsabackend.config.TestBeanConfig;
import com.jjbacsa.jjbacsabackend.search.entity.SearchEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestBeanConfig.class)
public class SearchRepositoryTest {

    @Autowired
    private SearchRepository searchRepository;

    private static SearchEntity search1;
    private static SearchEntity search2;
    private static SearchEntity search3;
    private static SearchEntity search4;
    private static SearchEntity search5;
    private static SearchEntity search6;
    private static SearchEntity search7;

    @BeforeAll
    static void init() {
        //검색어 생성
        search1 = SearchEntity.builder()
                .content("떡볶이")
                .build();

        search2 = SearchEntity.builder()
                .content("홍대 떡볶이")
                .build();

        search3 = SearchEntity.builder()
                .content("햄버거")
                .build();

        search4 = SearchEntity.builder()
                .content("맥도날드")
                .build();

        search5 = SearchEntity.builder()
                .content("아이스크림")
                .build();

        search6 = SearchEntity.builder()
                .content("홍대 맛집")
                .build();

        search7 = SearchEntity.builder()
                .content("토스트")
                .build();

        //score 변경
        search7.updateScore(Long.valueOf(7));
        search6.updateScore(Long.valueOf(6));
        search5.updateScore(Long.valueOf(5));
        search4.updateScore(Long.valueOf(4));
        search3.updateScore(Long.valueOf(3));
    }

    @Test
    public void saveTest() {
        SearchEntity searchEntity1 = searchRepository.save(search1);
        SearchEntity searchEntity2 = searchRepository.save(search2);
        SearchEntity searchEntity3 = searchRepository.save(search3);
        SearchEntity searchEntity4 = searchRepository.save(search4);
        SearchEntity searchEntity5 = searchRepository.save(search5);
        SearchEntity searchEntity6 = searchRepository.save(search6);
        SearchEntity searchEntity7 = searchRepository.save(search7);

        assertEquals(searchEntity1.getContent(), "떡볶이");
        assertEquals(searchEntity2.getContent(), "홍대 떡볶이");
        assertEquals(searchEntity3.getContent(), "햄버거");
        assertEquals(searchEntity4.getContent(), "맥도날드");
        assertEquals(searchEntity5.getContent(), "아이스크림");
        assertEquals(searchEntity6.getContent(), "홍대 맛집");
        assertEquals(searchEntity7.getContent(), "토스트");
    }

    @Test
    public void existsTest() {
        SearchEntity searchEntity1 = searchRepository.save(search1);
        SearchEntity searchEntity2 = searchRepository.save(search2);
        SearchEntity searchEntity3 = searchRepository.save(search3);
        SearchEntity searchEntity4 = searchRepository.save(search4);
        SearchEntity searchEntity5 = searchRepository.save(search5);
        SearchEntity searchEntity6 = searchRepository.save(search6);
        SearchEntity searchEntity7 = searchRepository.save(search7);

        assertTrue(searchRepository.existsByContent("떡볶이"));
        assertTrue(searchRepository.existsByContent("홍대 떡볶이"));
        assertTrue(searchRepository.existsByContent("햄버거"));
        assertTrue(searchRepository.existsByContent("맥도날드"));
        assertTrue(searchRepository.existsByContent("아이스크림"));
        assertTrue(searchRepository.existsByContent("홍대 맛집"));
        assertTrue(searchRepository.existsByContent("토스트"));

        assertFalse(searchRepository.existsByContent("빙수"));
    }

    @Test
    public void findByContentTest() {
        SearchEntity searchEntity1 = searchRepository.save(search1);
        assertEquals("떡볶이", searchRepository.findByContent("떡볶이").get().getContent());
    }

    @Test
    public void findTop5ByContentContainingTest() {
        SearchEntity searchEntity1 = searchRepository.save(search1);
        SearchEntity searchEntity2 = searchRepository.save(search2);
        SearchEntity searchEntity3 = searchRepository.save(search3);
        SearchEntity searchEntity4 = searchRepository.save(search4);
        SearchEntity searchEntity5 = searchRepository.save(search5);
        SearchEntity searchEntity6 = searchRepository.save(search6);
        SearchEntity searchEntity7 = searchRepository.save(search7);

        List<SearchEntity> res = searchRepository.findTop5ByContentContainingOrderByScoreDesc("떡");

        for (SearchEntity s : res) {
            System.out.println(s.getContent());
        }
        assertEquals(2, searchRepository.findTop5ByContentContainingOrderByScoreDesc("떡").size());
    }

}
