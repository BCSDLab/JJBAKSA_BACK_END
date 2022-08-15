package com.jjbacsa.jjbacsabackend.shop.serviceImpl;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.mapper.ShopMapper;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final WebClient webClient;
    private final String BASE_URL="https://maps.googleapis.com/maps/api/place";

    @Value("${external.api.key}")
    private String API_KEY;

    private ShopServiceImpl(WebClient.Builder webclientBuilder, ShopRepository shopRepository) {

        this.shopRepository=shopRepository;

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
    public ShopResponse getShop(String placeId) throws ParseException {

        if(shopRepository.existsByPlaceId(placeId)){
            Optional<ShopEntity> shopEntity=shopRepository.findByPlaceId(placeId);
            ShopResponse shopResponse=ShopMapper.INSTANCE.toShopResponse(shopEntity.get());

            return shopResponse;

        }else{
            ShopDto shopDto = getShopDetails(placeId);
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

    private ShopDto getShopDetails(String placeId) throws ParseException {
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

    private ShopDto jsonToShop(String jsonStr) throws ParseException {
        JSONParser jsonParser= new JSONParser();
        JSONObject jsonObject=(JSONObject) jsonParser.parse(jsonStr);

        String status=jsonObject.get("status").toString();
        if(!status.equals("OK"))
            throw new IllegalArgumentException("요청이 올바르지 않습니다.");

        JSONObject jsonObject_result=(JSONObject) jsonObject.get("result");

        String placeName= jsonObject_result.get("name").toString();
        String placeId=jsonObject_result.get("place_id").toString();

        JSONObject jsonObject_geometry=(JSONObject) jsonObject_result.get("geometry");
        JSONObject jsonObject_location=(JSONObject) jsonObject_geometry.get("location");

        String x= jsonObject_location.get("lat").toString();
        String y= jsonObject_location.get("lng").toString();

        JSONArray types_array=(JSONArray) jsonObject_result.get("types");
        String categoryName=types_array.get(0).toString();

        String address= jsonObject_result.get("formatted_address").toString();

        String phoneNumber;
        try{
            phoneNumber=jsonObject_result.get("formatted_phone_number").toString();
        }catch (NullPointerException e){
            phoneNumber=null;
        }

        JSONObject opening_hours=(JSONObject) jsonObject_result.get("opening_hours");

        String weekdayText;
        try{
            weekdayText=opening_hours.get("weekday_text").toString();
        }catch (NullPointerException e){
            weekdayText=null;
        }

        if(!(categoryName.equals("cafe")||categoryName.equals("restaurant")))
            throw new RuntimeException("카페/맛집이 아닙니다.");


        ShopDto shopDto = ShopDto.builder()
                .placeId(placeId)
                .placeName(placeName)
                .x(x)
                .y(y)
                .address(address)
                .phone(phoneNumber)
                .businessDay(weekdayText)
                .categoryName(categoryName)
                .build();

        return shopDto;
    }
}


