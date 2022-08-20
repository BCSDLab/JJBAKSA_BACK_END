package com.jjbacsa.jjbacsabackend.shop.serviceImpl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopApiDto;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.mapper.ShopMapper;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final WebClient webClient;
    private final String BASE_URL="https://maps.googleapis.com/maps/api/place";

    private final ObjectMapper objectMapper;

    @Value("${external.api.key}")
    private String API_KEY;

    private ShopServiceImpl(WebClient.Builder webclientBuilder, ShopRepository shopRepository, ObjectMapper objectMapper) {

        this.shopRepository=shopRepository;
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

    @Override
    public ShopResponse searchShop(String keyword) throws ParseException {
        Optional<ShopEntity> savedShop=shopRepository.findByPlaceNameContaining(keyword);

        if(savedShop.isPresent())
            return ShopMapper.INSTANCE.toShopResponse(savedShop.get());
        else
            throw new NullPointerException("해당하는 가게가 없습니다.");

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
        String resultStr=objectMapper.writeValueAsString(map.get("result"));
        ShopApiDto shopApiDto=objectMapper.readValue(resultStr, ShopApiDto.class);

        return shopApiDto;
    }
}