package com.jjbacsa.jjbacsabackend.search.serviceImpl;

import com.jjbacsa.jjbacsabackend.search.dto.AutoCompleteResponse;
import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.entity.SearchEntity;
import com.jjbacsa.jjbacsabackend.search.repository.SearchRepository;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final StringRedisTemplate redisTemplate;
    private final SearchRepository searchRepository;

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
}
