package com.jjbacsa.jjbacsabackend.search.serviceImpl;

import com.jjbacsa.jjbacsabackend.search.dto.AutoCompleteResponse;
import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.entity.SearchEntity;
import com.jjbacsa.jjbacsabackend.search.repository.SearchRepository;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final StringRedisTemplate redisTemplate;
    private final SearchRepository searchRepository;
    private final String KEY = "RANKING";

    @Transactional(readOnly = true)
    @Override
    public AutoCompleteResponse getAutoCompletes(String word) {
        List<SearchEntity> autoCompletes = searchRepository.findTop5ByContentContainingOrderByScoreDesc(word);
        List<String> autoCompletesStrs = new ArrayList<>();

        for (SearchEntity ac : autoCompletes) {
            autoCompletesStrs.add(ac.getContent());
        }

        return AutoCompleteResponse.builder().autoCompletes(autoCompletesStrs).build();
    }


    @Override
    public TrendingResponse getTrending(String key) {
        return TrendingResponse.builder().
                trendings(redisTemplate.opsForZSet().reverseRange(key, 0, -1).stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public void saveRedis(String keyword, String key) {
        List<String> rankingList = redisTemplate.opsForZSet().reverseRange(key, 0, -1).stream().collect(Collectors.toList());

        redisTemplate.opsForZSet().incrementScore(key, keyword, 2);

        for (String ranking : rankingList) {
            if (!ranking.equals(keyword)) {
                redisTemplate.opsForZSet().incrementScore(key, ranking, -1);
            }
        }

        long size = redisTemplate.opsForZSet().zCard(key);
        if (size > 10) {
            long offset = size - 10;
            redisTemplate.opsForZSet().removeRange(key, 0, offset - 1);
        }

    }

    @Transactional
    @Override
    public void saveForAutoComplete(String keyword) {
        if (searchRepository.existsByContent(keyword)) {
            SearchEntity searchEntity = searchRepository.findByContent(keyword).get();
            Long latestScore = searchEntity.getScore();
            searchEntity.updateScore(latestScore + 1);
        } else {
            SearchEntity searchEntity = SearchEntity.builder()
                    .content(keyword)
                    .build();

            searchRepository.save(searchEntity);
        }
    }
}
