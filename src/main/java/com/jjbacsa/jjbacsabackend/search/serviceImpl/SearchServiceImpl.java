package com.jjbacsa.jjbacsabackend.search.serviceImpl;

import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final StringRedisTemplate redisTemplate;
    private final String ranking;

    public SearchServiceImpl(StringRedisTemplate redisTemplate, @Value("${spring.redis.ranking}") String ranking) {
        this.redisTemplate = redisTemplate;
        this.ranking = ranking;

    }

    @Override
    public TrendingResponse getTrending() {
        return TrendingResponse.builder().
                trendings(redisTemplate.opsForZSet().reverseRange(ranking, 0, -1).stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public void saveTrending(String keyword) {
        List<String> rankingList = redisTemplate.opsForZSet().reverseRange(ranking, 0, -1).stream().collect(Collectors.toList());

        redisTemplate.opsForZSet().incrementScore(ranking, keyword, 2);

        for (String ranking : rankingList) {
            if (!ranking.equals(keyword)) {
                redisTemplate.opsForZSet().incrementScore(this.ranking, ranking, -1);
            }
        }

        long size = redisTemplate.opsForZSet().zCard(ranking);
        if (size > 10) {
            long offset = size - 10;
            redisTemplate.opsForZSet().removeRange(ranking, 0, offset - 1);
        }
    }
}
