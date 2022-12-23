package com.jjbacsa.jjbacsabackend.shop.serviceimpl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.etc.exception.CriticalException;
import com.jjbacsa.jjbacsabackend.shop.dto.*;
import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.TrendingResponse;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.mapper.ShopMapper;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final WebClient webClient;
    private final String BASE_URL="https://maps.googleapis.com/maps/api/place";
    private final StringRedisTemplate redisTemplate;
    private final String KEY="ranking";

    private final List<String>cafe= Arrays.asList("카페","디저트","커피","후식");
    private final List<String>restaurant=Arrays.asList("맛집","식당","레스토랑","음식점");

    private final ObjectMapper objectMapper;

    private String API_KEY;

    public ShopServiceImpl(ShopRepository shopRepository, ObjectMapper objectMapper,StringRedisTemplate redisTemplate,@Value("${external.api.key}") String key) {

        this.shopRepository=shopRepository;
        this.redisTemplate=redisTemplate;
        this.objectMapper=objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        this.API_KEY=key;

        DefaultUriBuilderFactory factory=new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        ClientHttpConnector clientHttpConnector=new ReactorClientHttpConnector(httpClient);

        this.webClient = WebClient.builder().clientConnector(clientHttpConnector)
                .uriBuilderFactory(factory).baseUrl(BASE_URL)
                .build();
    }

    @Transactional
    @Override
    public ShopResponse getShop(String placeId){

        if(shopRepository.existsByPlaceId(placeId)){
            Optional<ShopEntity> shopEntity=shopRepository.findByPlaceId(placeId);

            ShopResponse shopResponse=ShopMapper.INSTANCE.toShopResponse(shopEntity.get());
            shopResponse.setShopCount(shopRepository.getTotalRating(shopResponse.getShopId()), shopRepository.getRatingCount(shopResponse.getShopId()));

            return shopResponse;
        }else{
            ShopApiDto shopApiDto;

            try{
                shopApiDto=getShopDetails(placeId);
            }catch(JsonProcessingException e){
                throw new CriticalException(ErrorMessage.JSON_PROCESSING_EXCEPTION);
            }catch(ApiException apiException){
                throw apiException;
            }

            ShopDto shopDto=ShopDto.ShopDto(shopApiDto);
            ShopEntity shopEntity=register(shopDto);

            ShopResponse shopResponse=ShopMapper.INSTANCE.toShopResponse(shopEntity);
            shopResponse.setShopCount(shopRepository.getTotalRating(shopResponse.getShopId()), shopRepository.getRatingCount(shopResponse.getShopId()));

            return shopResponse;
        }
    }

    private ShopEntity register(ShopDto shopDto) {
        ShopEntity shopEntity= ShopMapper.INSTANCE.toEntity(shopDto);

        return shopRepository.save(shopEntity);
    }

    private ShopApiDto getShopDetails(String placeId) throws JsonProcessingException {
        String shopStr=webClient.get().uri(uriBuilder ->
                uriBuilder.path("/details/json")
                        .queryParam("place_id",placeId)
                        .queryParam("language","ko")
                        .queryParam("key",API_KEY)
                        .queryParam("fields","formatted_address,formatted_phone_number,name,geometry/location/lat,geometry/location/lng,types,place_id,opening_hours/weekday_text")
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return jsonToShop(shopStr);
    }

    private ShopApiDto jsonToShop(String jsonStr) throws JsonProcessingException {

        Map<String,Object> map=objectMapper.readValue(jsonStr, new TypeReference<HashMap<String, Object>>() {});

        String status=(String)map.get("status");

        if(!status.equals("OK")) {
            switch(status){
                case "ZERO_RESULTS":
                    throw new ApiException(ErrorMessage.ZERO_RESULTS_EXCEPTION);
                case "NOT_FOUND":
                    throw new ApiException(ErrorMessage.NOT_FOUND_EXCEPTION);
                case "INVALID_REQUEST":
                    throw new ApiException(ErrorMessage.INVALID_REQUEST_EXCEPTION);
                case "OVER_QUERY_LIMIT":
                    throw new ApiException(ErrorMessage.OVER_QUERY_LIMIT_EXCEPTION);
                case "REQUEST_DENIED":
                    throw new ApiException(ErrorMessage.REQUEST_DENIEDE_EXCEPTION);
                case "UNKNOWN_ERROR":
                    throw new ApiException(ErrorMessage.UNDEFINED_EXCEPTION);
            }
        }

        String resultStr=objectMapper.writeValueAsString(map.get("result"));
        ShopApiDto shopApiDto=objectMapper.readValue(resultStr, ShopApiDto.class);

        return shopApiDto;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShopSummaryResponse> searchShop(ShopRequest shopRequest, Integer page,Integer size) {
        //keyword Redis 저장
        String keyword=shopRequest.getKeyword();
        saveRedis(keyword);

        //키워드 검색 타입 판별
        SearchType searchType=typeSetting(keyword);
        List<ShopSummaryResponse> shopList=new ArrayList<>();
        String keywordForQuery;

        switch (searchType){
            case cafe:
            case restaurant:
                //키워드 정제
                keywordForQuery=getKeywordForQuery(keyword);
                shopList.addAll(shopRepository.search(keywordForQuery,searchType.name()));
                break;
            case cafe_category:
                shopList.addAll(shopRepository.findAllByCategoryName("cafe"));
                break;
            case restaurant_category:
                shopList.addAll(shopRepository.findAllByCategoryName("restaurant"));
                break;
            case one:
                shopList.addAll(shopRepository.findByPlaceNameContaining(keyword));
                break;
            case NONE:
                keywordForQuery=getKeywordForQuery(keyword);
                shopList.addAll(shopRepository.search(keywordForQuery,null));
                break;
        }

        //거리 계산
        for(ShopSummaryResponse shop:shopList){
            shop.setDist(shopRequest.getX(), shopRequest.getY());
        }

        if(searchType!=SearchType.cafe_category&&searchType!=SearchType.restaurant_category&&searchType!=SearchType.one) {
            //정확도 순으로 정렬(거리순은 2차 정렬)
            Collections.sort(shopList, new Comparator<ShopSummaryResponse>() {
                @Override
                public int compare(ShopSummaryResponse s1, ShopSummaryResponse s2) {
                    if (s1.getScore() < s2.getScore()) return 1;
                    else if (s1.getScore() == s2.getScore())
                        return s1.compareTo(s2);
                    else
                        return -1;
                }
            });
        } else{
            Collections.sort(shopList);
        }

        //pagination
        Pageable pageable= PageRequest.of(page,size);

        int start=Math.min((int)pageable.getOffset(),shopList.size());
        int end=(start+pageable.getPageSize()>shopList.size()? shopList.size() : (start+ pageable.getPageSize()));

        return new PageImpl<>(shopList.subList(start,end),pageable,shopList.size());
    }

    private SearchType typeSetting(String keyword){

        if(keyword.length()==1){
            return SearchType.one;
        }

        for(String str:cafe){
            if(keyword.equals(str)){
                return SearchType.cafe_category;
            }

            if(keyword.contains(str)){
                return SearchType.cafe;
            }
        }

        for(String str:restaurant){
            if(keyword.equals(str)){
                return SearchType.restaurant_category;
            }

            if(keyword.contains(str)){
                return SearchType.restaurant;
            }
        }

        return SearchType.NONE;
    }

    //키워드 정제
    private String getKeywordForQuery(String keyword){
        String replaceKeyword=keyword.replace(" ","");
        int length=replaceKeyword.length();

        String resString="";

        for(int i=0;i<length-1;i++){
            resString+=keyword.substring(i,i+2);
            resString+=" ";
        }

        return resString.substring(0,resString.length()-1);
    }

    private void saveRedis(String keyword){
        List<String> rankingList=redisTemplate.opsForZSet().reverseRange(KEY,0,-1).stream().collect(Collectors.toList());

        redisTemplate.opsForZSet().incrementScore(KEY,keyword,2);

        for(String ranking:rankingList){
            if(!ranking.equals(keyword)){
                redisTemplate.opsForZSet().incrementScore(KEY,ranking,-1);
            }
        }

        long size=redisTemplate.opsForZSet().zCard(KEY);
        if(size>10){
            long offset=size-10;
            redisTemplate.opsForZSet().removeRange(KEY,0,offset-1);
        }

    }

    @Override
    public TrendingResponse getTrending() {
        return TrendingResponse.builder().
                trendings(redisTemplate.opsForZSet().reverseRange(KEY,0,-1).stream().collect(Collectors.toList()))
                .build();
    }

}
enum SearchType{
    cafe, restaurant, NONE, cafe_category,restaurant_category, one
}