package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.google.dto.ShopApiDto;
import com.jjbacsa.jjbacsabackend.google.dto.ShopQueryApiDto;
import com.jjbacsa.jjbacsabackend.google.dto.ShopQueryDto;
import com.jjbacsa.jjbacsabackend.google.dto.SimpleShopDto;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponse;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.service.GoogleService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class GoogleServiceImpl implements GoogleService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/place";
    private final String API_KEY;
    private final GoogleShopRepository googleShopRepository;
    private final List<String> simpleFields;

    public GoogleServiceImpl(ObjectMapper objectMapper, @Value("${external.api.key}") String key, GoogleShopRepository googleShopRepository) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        this.API_KEY = key;
        this.googleShopRepository = googleShopRepository;
        this.simpleFields = List.of("geometry/location/lat", "geometry/location/lng", "name", "place_id");

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        this.webClient = WebClient.builder().clientConnector(clientHttpConnector)
                .uriBuilderFactory(factory).baseUrl(BASE_URL)
                .build();
    }

    @Override
    public ShopQueryResponses searchShopQuery(String query, double x, double y) throws JsonProcessingException {
        String shopStr = null;

        shopStr = this.callApiByQuery(query, x, y);

        ShopQueryDto shopQueryDto = this.jsonToShopQueryDto(shopStr);
        ShopQueryResponses shopQueryResponses = this.queryDtoToQueryResponses(shopQueryDto, x, y);

        return shopQueryResponses;
    }

    @Override
    public ShopQueryResponses searchShopQueryNext(String pageToken, double x, double y) throws JsonProcessingException {
        ShopQueryDto shopQueryDto;

        String shopStr = this.callApiByQuery(pageToken);
        shopQueryDto = this.jsonToShopQueryDto(shopStr);

        ShopQueryResponses shopQueryResponses = this.queryDtoToQueryResponses(shopQueryDto, x, y);
        return shopQueryResponses;
    }

    @Override
    public ShopResponse getShopDetails(String placeId, double x, double y) throws JsonProcessingException {
        ShopApiDto shopApiDto;

        String shopStr = this.callGoogleApi(placeId);
        shopApiDto = this.jsonToShopApiDto(shopStr);


        String xTemp;
        String yTemp;
        try {
            xTemp = shopApiDto.getGeometry().getLocation().getLat();
            yTemp = shopApiDto.getGeometry().getLocation().getLng();
        } catch (NullPointerException e) {
            xTemp = null;
            yTemp = null;
        }

        String businessDay;
        try {
            JSONArray jsonArray = new JSONArray();
            for (String weekday : shopApiDto.getOpening_hours().getWeekday_text()) {
                jsonArray.add(weekday);
            }

            businessDay = jsonArray.toJSONString();
        } catch (NullPointerException e) {
            businessDay = null;
        }

        Boolean openNow;
        try {
            openNow = (shopApiDto.getOpening_hours().getOpen_now() == "true") ? true : false;
        } catch (NullPointerException e) {
            openNow = null;
        }

        String token;
        try {
            token = shopApiDto.getPhotos().get(0).getPhoto_reference();
        } catch (NullPointerException e) {
            token = null;
        }

        Category category = getCategory(shopApiDto.getTypes());

        ShopResponse shopResponse = ShopResponse.builder()
                .place_id(shopApiDto.getPlace_id())
                .name(shopApiDto.getName())
                .formatted_address(shopApiDto.getFormatted_address())
                .formatted_phone_number(shopApiDto.getFormatted_phone_number())
                .x(xTemp)
                .y(yTemp)
                .open_now(openNow)
                .photoToken(token)
                .businessDay(businessDay)
                .category(category.name())
                .build();

        if (shopResponse.getX() != null && shopResponse.getY() != null) {
            shopResponse.setDist(x, y);
        } else {
            shopResponse.setDist();
        }

        Optional<GoogleShopEntity> shop = googleShopRepository.findByPlaceId(shopResponse.getPlace_id());

        if (shop.isPresent()) {
            GoogleShopCount shopCount = shop.get().getShopCount();
            shopResponse.setShopCount(shopCount.getTotalRating(), shopCount.getRatingCount());
        }

        return shopResponse;
    }

    private ShopQueryResponses queryDtoToQueryResponses(ShopQueryDto shopQueryDto, double x, double y) {
        List<ShopQueryResponse> shopQueryResponseList = new ArrayList<>();

        for (ShopQueryApiDto dto : shopQueryDto.getResults()) {
            String xTemp;
            String yTemp;

            try {
                xTemp = dto.getGeometry().getLocation().getLat();
            } catch (NullPointerException e) {
                xTemp = null;
            }

            try {
                yTemp = dto.getGeometry().getLocation().getLng();
            } catch (NullPointerException e) {
                yTemp = null;
            }

            Boolean openNow;
            try {
                openNow = (dto.getOpening_hours().getOpen_now().equals("true")) ? true : false;
            } catch (NullPointerException e) {
                openNow = null;
            }

            String token;
            try {
                token = dto.getPhotos().get(0).getPhoto_reference();
            } catch (NullPointerException e) {
                token = null;
            }

            Category category = getCategory(dto.getTypes());

            ShopQueryResponse shopQueryResponse = ShopQueryResponse.builder()
                    .place_id(dto.getPlace_id())
                    .name(dto.getName())
                    .formatted_address(dto.getFormatted_address())
                    .x(xTemp)
                    .y(yTemp)
                    .open_now(openNow)
                    .photoToken(token)
                    .category(category.name())
                    .build();

            if (shopQueryResponse.getX() != null && shopQueryResponse.getY() != null) {
                shopQueryResponse.setDist(x, y);
            } else {
                shopQueryResponse.setDist();
            }

            Optional<GoogleShopEntity> shop = googleShopRepository.findByPlaceId(dto.getPlace_id());

            if (shop.isPresent()) {
                GoogleShopCount shopCount = shop.get().getShopCount();
                shopQueryResponse.setShopCount(shopCount.getTotalRating(), shopCount.getRatingCount());
            }

            shopQueryResponseList.add(shopQueryResponse);
        }

        ShopQueryResponses shopQueryResponses = new ShopQueryResponses(shopQueryDto.getNext_page_token(), shopQueryResponseList);
        return shopQueryResponses;
    }

    //현재 사용자가 리뷰랑 북마크를 한 음식점에서 반경 안에 있는 음식점
    @Override
    public List<SimpleShopDto> getNearByShops(double x, double y, double radius) {
        return null;
    }

    //현재 사용자의 친구가 리뷰를 한 음식점 (명확하지가 않음)
    @Override
    public List<SimpleShopDto> getFriendsShops(double x, double y, double radius) {
        return null;
    }

    //현재 사용자가 북마크를 한 음식점
    @Override
    public List<SimpleShopDto> getBookMarkShops(double x, double y, double radius) {
        return null;
    }

    /**
     * 여러 place_ids에서 간단하게 상점 정보를 받아오는 메소드
     */
    private List<SimpleShopDto> callApiByPlaceIdsNonBlocking(List<String> placeIds, List<String> fields) throws JsonProcessingException {
        List<Mono<String>> monos = new ArrayList<>();

        for (String id : placeIds) {
            Mono<String> shopStr;

            shopStr = webClient.get().uri(uriBuilder ->
                            uriBuilder.path("/details/json")
                                    .queryParam("place_id", id)
                                    .queryParam("language", "ko")
                                    .queryParam("key", API_KEY)
                                    .queryParam("fields", fields)
                                    .build())
                    .retrieve().bodyToMono(String.class);

            monos.add(shopStr);
        }

        Function<Object[], List> combinator = monoList -> Arrays.stream(monoList).collect(Collectors.toList());
        List<String> results = Mono.zip(monos, combinator).block();

        List<SimpleShopDto> simpleShopDtos = new ArrayList<>();
        int continualException = 0;
        for (String result : results) {
            if (continualException > 5) {
                throw new ApiException(ErrorMessage.CONTINUAL_API_EXCEPTION);
            }
            try {
                continualException = 0;
                SimpleShopDto simpleShopDto = this.jsonToSimpleShopDto(result);
                simpleShopDtos.add(simpleShopDto);
            } catch (ApiException e) {
                continualException++;
            }
        }
        return simpleShopDtos;
    }



    /**
     * 검색어를 통한 상점검색 내부 메소드
     *
     * @param query 검색어
     * @return block으로 받아온 결과
     */
    private String callApiByQuery(String query, double x, double y) {

        String locationQuery = String.valueOf(x) + ", " + String.valueOf(y);

        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/textsearch/json")
                        .queryParam("query", query)
                        .queryParam("key", API_KEY)
                        .queryParam("language", "ko")
                        .queryParam("type", "food")
                        .queryParam("location", locationQuery)
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    /**
     * 검색어를 통한 상점검색 중 다음 페이지 요청 메소드
     *
     * @param pageToken 다음 페이지 토큰
     * @return block으로 받아온 결과
     */
    private String callApiByQuery(String pageToken) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/textsearch/json")
                        .queryParam("pagetoken", pageToken)
                        .queryParam("key", API_KEY)
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    /**
     * 상점 단일검색을 위한 메소드
     *
     * @param placeId 구글에서 발행한 상점 아이디
     * @return block으로 받아온 단일 상점 결과
     */
    private String callGoogleApi(String placeId) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("language", "ko")
                        .queryParam("key", API_KEY)
                        .queryParam("fields", "formatted_address,formatted_phone_number,name,geometry/location/lat,geometry/location/lng,types,place_id,opening_hours/open_now,opening_hours/weekday_text")
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    /**
     * Simple 상점 DTO 파싱 메소드
     */
    private SimpleShopDto jsonToSimpleShopDto(String shopStr) throws JsonProcessingException {
        Map<String, Object> map = this.checkApiReturn(shopStr);

        String resultStr = objectMapper.writeValueAsString(map.get("result"));
        SimpleShopDto simpleShopDto = objectMapper.readValue(resultStr, SimpleShopDto.class);

        return simpleShopDto;
    }

    /**
     * 단일 상점 DTO 파싱 메소드
     */
    private ShopApiDto jsonToShopApiDto(String shopStr) throws JsonProcessingException {

        Map<String, Object> map = this.checkApiReturn(shopStr);

        String resultStr = objectMapper.writeValueAsString(map.get("result"));
        ShopApiDto shopApiDto = objectMapper.readValue(resultStr, ShopApiDto.class);

        return shopApiDto;
    }

    /**
     * 다중 상점 DTO 파싱 메소드
     */
    private ShopQueryDto jsonToShopQueryDto(String shopStr) throws JsonProcessingException {

        this.checkApiReturn(shopStr);
        ShopQueryDto shopQueryDto = objectMapper.readValue(shopStr, ShopQueryDto.class);

        return shopQueryDto;
    }

    //api return status check
    private Map<String, Object> checkApiReturn(String apiReturn) throws JsonProcessingException {
        Map<String, Object> map = objectMapper.readValue(apiReturn, new TypeReference<HashMap<String, Object>>() {
        });

        String status = (String) map.get("status");

        if (!status.equals("OK")) {
            switch (status) {
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

        return map;
    }

    @Override
    public void saveGoogleShop(String placeId) {
        GoogleShopEntity shopEntity = GoogleShopEntity.builder()
                .placeId(placeId)
                .build();

        this.googleShopRepository.save(shopEntity);
    }

    private Category getCategory(List<String> types) {
        for (String type : types) {
            if (type.equals("cafe")) {
                return Category.cafe;
            }
        }
        return Category.restaurant;
    }
}

enum Category {
    cafe, restaurant;
}
