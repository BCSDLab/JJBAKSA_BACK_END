package com.jjbacsa.jjbacsabackend.search.serviceImpl;

import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final StringRedisTemplate redisTemplate;
    private final String RANKING = "RANKING";

    @Override
    public TrendingResponse getTrending() {
        return TrendingResponse.builder().
                trendings(redisTemplate.opsForZSet().reverseRange(RANKING, 0, -1).stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public void saveTrending(String keyword) {
        List<String> rankingList = redisTemplate.opsForZSet().reverseRange(RANKING, 0, -1).stream().collect(Collectors.toList());

        redisTemplate.opsForZSet().incrementScore(RANKING, keyword, 2);

        for (String ranking : rankingList) {
            if (!ranking.equals(keyword)) {
                redisTemplate.opsForZSet().incrementScore(RANKING, ranking, -1);
            }
        }

        long size = redisTemplate.opsForZSet().zCard(RANKING);
        if (size > 10) {
            long offset = size - 10;
            redisTemplate.opsForZSet().removeRange(RANKING, 0, offset - 1);
        }
    }
}
