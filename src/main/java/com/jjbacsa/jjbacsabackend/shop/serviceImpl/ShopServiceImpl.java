package com.jjbacsa.jjbacsabackend.shop.serviceImpl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.shop.dto.*;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.mapper.ShopMapper;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
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

    private final List<String>cafe= Arrays.asList("카페","디저트","커피");
    private final List<String>restaurant=Arrays.asList("맛집","식당","레스토랑");

    private final ObjectMapper objectMapper;

    @Value("${external.api.key}")
    private String API_KEY;

    private ShopServiceImpl(WebClient.Builder webclientBuilder, ShopRepository shopRepository, ObjectMapper objectMapper,StringRedisTemplate redisTemplate) {

        this.shopRepository=shopRepository;
        this.redisTemplate=redisTemplate;
        this.objectMapper=objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        DefaultUriBuilderFactory factory=new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        this.webClient = webclientBuilder.uriBuilderFactory(factory).baseUrl(BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    public ShopResponse getShop(String placeId) throws JsonProcessingException {

        if(shopRepository.existsByPlaceId(placeId)){
            Optional<ShopEntity> shopEntity=shopRepository.findByPlaceId(placeId);
            ShopResponse shopResponse=ShopMapper.INSTANCE.toShopResponse(shopEntity.get());

            return shopResponse;

        }else{
            ShopApiDto shopApiDto = getShopDetails(placeId);
            ShopDto shopDto=ShopDto.ShopDto(shopApiDto);
            ShopEntity shopEntity=register(shopDto);

            return ShopMapper.INSTANCE.toShopResponse(shopEntity);
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
        Map<String,Object> map=objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
        String status=(String)map.get("status");

        if(!status.equals("OK"))
            throw new IllegalArgumentException("place_id가 유효하지 않습니다.");

        String resultStr=objectMapper.writeValueAsString(map.get("result"));
        ShopApiDto shopApiDto=objectMapper.readValue(resultStr, ShopApiDto.class);

        return shopApiDto;
    }

    //DB내 상점검색
    //정확도 -> 거리순
    @Override
    public Page<ShopSummary> searchShop(ShopRequest shopRequest) {
        //keyword Redis 저장
        redisTemplate.opsForZSet().incrementScore(KEY,shopRequest.getKeyword(),1);

        String keyword=shopRequest.getKeyword();

        //키워드 검색 타입 판별
        SearchType searchType=typeSetting(keyword);

        //검색 정제
        List<ShopSummary> shopList=new ArrayList<>();

        switch (searchType){
            case cafe:
            case restaurant:
                shopList.addAll(shopRepository.searchWithCategory(keyword,searchType.name()).stream().map(t -> new ShopSummary(
                                t.get(0,String.class),
                                t.get(1,String.class),
                                t.get(2,String.class),
                                t.get(3,String.class),
                                t.get(4,String.class),
                                t.get(5,Double.class)
                        ))
                        .collect(Collectors.toList())
                );
                break;
            case NONE:
                shopList.addAll(shopRepository.search(keyword).stream().map(t -> new ShopSummary(
                                        t.get(0,String.class),
                                        t.get(1,String.class),
                                        t.get(2,String.class),
                                        t.get(3,String.class),
                                        t.get(4,String.class),
                                        t.get(5,Double.class)
                                ))
                                .collect(Collectors.toList())
                );
                break;
        }

        //거리 계산
        for(ShopSummary shop:shopList){
            shop.setDist(shopRequest.getX(), shopRequest.getY());
        }

        //거리순으로 정렬
        Collections.sort(shopList);

        //정확도 순으로 정렬
        Comparator<ShopSummary> cp=new Comparator<ShopSummary>() {
            @Override
            public int compare(ShopSummary o1, ShopSummary o2) {
                return (int) (o1.getDist()-o2.getDist());
            }
        };

        Collections.sort(shopList,cp);

        //pagination
        Pageable pageable=shopRequest.getPageable();
        int start=(int)pageable.getOffset();
        int end=(start+pageable.getPageSize()>shopList.size()? shopList.size() : (start+ pageable.getPageSize()));

        return new PageImpl<>(shopList.subList(start,end),pageable,shopList.size());
    }

    private SearchType typeSetting(String keyword){

        for(String str:cafe){
            if(keyword.contains(str)){
                return SearchType.cafe;
            }
        }

        for(String str:restaurant){
            if(keyword.contains(str)){
                return SearchType.restaurant;
            }
        }

        return SearchType.NONE;
    }

}
enum SearchType{
    cafe, restaurant, NONE
}