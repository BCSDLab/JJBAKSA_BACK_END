//package com.jjbacsa.jjbacsabackend.search.service;
//
//import com.jjbacsa.jjbacsabackend.search.dto.AutoCompleteResponse;
//import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
//import com.jjbacsa.jjbacsabackend.search.entity.SearchEntity;
//import com.jjbacsa.jjbacsabackend.search.repository.SearchRepository;
//import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.context.TestConstructor;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Redis Rollback 불가능
// * -> SessionCallback 써도 불가해서 별도의 키 생성 후 삭제하는 방식으로 테스트 코드 작성
// */
//
//@RequiredArgsConstructor
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//@SpringBootTest
//public class SearchServiceTest {
//
//    private final SearchService searchService;
//    private final ShopService shopService;
//    private final StringRedisTemplate redisTemplate;
//    private final String testKey = "rankingTest";
//    private final SearchRepository searchRepository;
//
//
//    @Test
//    public void not_trending() {
//        redisTemplate.delete(testKey);
//
//        TrendingResponse trendingResponse = searchService.getTrending();
//        assertEquals(trendingResponse.getTrendings().size(), 0);
//    }
//
//
//    @Test
//    public void trending_1() {
//        redisTemplate.opsForZSet().incrementScore(testKey, "떡볶이", 1);
//        redisTemplate.opsForZSet().incrementScore(testKey, "떡볶이", 1);
//        redisTemplate.opsForZSet().incrementScore(testKey, "떡볶이", 1);
//
//        TrendingResponse trendingResponse = searchService.getTrending();
//        assertEquals(trendingResponse.getTrendings().size(), 1);
//
//        redisTemplate.delete(testKey);
//    }
//
//    @Test
//    public void trending_2() {
//
//        String[] keywords = {"떡볶이", "라멘", "순대볶음", "서촌 맛집", "육회", "중국집", "맥도날드", "햄버거", "홍대 라멘", "마제소바", "이삭토스트"};
//        for (int i = 0; i < 11; i++) {
//            for (int j = 0; j < i + 1; j++) {
//                shopService.saveRedis(keywords[i]);
//            }
//        }
//
//        TrendingResponse trendingResponse = searchService.getTrending();
//        assertEquals(trendingResponse.getTrendings().size(), 10);
//
//        for (String trend : trendingResponse.getTrendings()) {
//            assertNotEquals(trend, "떡볶이");
//        }
//
//        redisTemplate.delete(testKey);
//    }
//
//    @Test
//    @Transactional
//    public void auto_complete() {
//        saveForAutoComplete("떡볶이");
//        saveForAutoComplete("떡볶이");
//
//        saveForAutoComplete("합정 떡볶이");
//        saveForAutoComplete("마포 떡볶이");
//        saveForAutoComplete("서촌 떡볶이");
//
//        saveForAutoComplete("즉석 떡볶이");
//        saveForAutoComplete("즉석 떡볶이");
//
//        saveForAutoComplete("홍대 떡볶이");
//        saveForAutoComplete("떡볶이 뷔페");
//
//        AutoCompleteResponse autoCompleteResponse = searchService.getAutoCompletes("떡");
//        assertEquals(autoCompleteResponse.getAutoCompletes().size(), 5);
//
//        /**
//         * 기존 autoComplete 데이터가 중첩된다면 테스트 실패할 수 있음
//         * */
//        assertEquals(autoCompleteResponse.getAutoCompletes().get(0), "떡볶이");
//        assertEquals(autoCompleteResponse.getAutoCompletes().get(1), "즉석 떡볶이");
//    }
//
//    private void saveForAutoComplete(String keyword) {
//        if (searchRepository.existsByContent(keyword)) {
//            SearchEntity searchEntity = searchRepository.findByContent(keyword).get();
//            Long latestScore = searchEntity.getScore();
//            searchEntity.updateScore(latestScore + 1);
//
//        } else {
//            searchRepository.save(new SearchEntity(keyword, 1L));
//        }
//    }
//}
