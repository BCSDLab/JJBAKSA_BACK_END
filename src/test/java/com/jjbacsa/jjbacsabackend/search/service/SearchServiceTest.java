package com.jjbacsa.jjbacsabackend.search.service;

import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.serviceImpl.SearchServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SearchServiceTest {
    private SearchService searchService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    private String testKey = "rankingTest";

    @BeforeEach
    void init() {
        searchService = new SearchServiceImpl(redisTemplate, testKey);
        redisTemplate.delete(testKey);
    }

    @AfterAll
    void after() {
        redisTemplate.delete(testKey);
    }

    @Test
    public void not_trending() {
        redisTemplate.delete(testKey);

        TrendingResponse trendingResponse = searchService.getTrending();
        assertEquals(trendingResponse.getTrendings().size(), 0);
    }

    @Test
    public void trending_1() {
        redisTemplate.delete(testKey);

        redisTemplate.opsForZSet().incrementScore(testKey, "떡볶이", 1);
        redisTemplate.opsForZSet().incrementScore(testKey, "떡볶이", 1);
        redisTemplate.opsForZSet().incrementScore(testKey, "떡볶이", 1);

        TrendingResponse trendingResponse = searchService.getTrending();
        assertEquals(trendingResponse.getTrendings().size(), 1);

        redisTemplate.delete(testKey);
    }

    @Test
    public void trending_2() {
        redisTemplate.delete(testKey);

        String[] keywords = {"떡볶이", "라멘", "순대볶음", "서촌 맛집", "육회", "중국집", "맥도날드", "햄버거", "홍대 라멘", "마제소바", "이삭토스트"};
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < i + 1; j++) {
                searchService.saveTrending(keywords[i]);
            }
        }

        TrendingResponse trendingResponse = searchService.getTrending();
        assertEquals(trendingResponse.getTrendings().size(), 10);

        for (String trend : trendingResponse.getTrendings()) {
            assertNotEquals(trend, "떡볶이");
        }

        redisTemplate.delete(testKey);
    }
}
