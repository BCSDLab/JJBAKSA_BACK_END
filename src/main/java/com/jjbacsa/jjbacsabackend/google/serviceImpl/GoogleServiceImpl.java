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
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class GoogleServiceImpl implements GoogleService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/place";
    private final String API_KEY;
    private final GoogleShopRepository googleShopRepository;

    public GoogleServiceImpl(ObjectMapper objectMapper, @Value("${external.api.key}") String key, GoogleShopRepository googleShopRepository) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        this.API_KEY = key;
        this.googleShopRepository = googleShopRepository;

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
    public ShopQueryResponses searchShopQuery(String query, String type, double x, double y) throws JsonProcessingException {
        String shopStr = null;

        if (type.equals("cafe")) {
            shopStr = this.callApiByCafeQuery(query);
        } else if (type.equals("restaurant")) {
            shopStr = this.callApiByRestaurantQuery(query);
        } else {
            //exception 처리
        }

        ShopQueryDto shopQueryDto = this.jsonToShopQueryDto(shopStr);
        ShopQueryResponses shopQueryResponses = this.queryDtoToQueryResponses(shopQueryDto, x, y);

        return shopQueryResponses;
    }

    @Override
    public ShopQueryResponses searchShopQueryNext(String pageToken, double x, double y) throws JsonProcessingException {
        ShopQueryDto shopQueryDto;

        String shopStr = this.callApiByNextTokenPage(pageToken);
        shopQueryDto = this.jsonToShopQueryDto(shopStr);

        ShopQueryResponses shopQueryResponses = this.queryDtoToQueryResponses(shopQueryDto, x, y);
        return shopQueryResponses;
    }

    @Override
    public ShopResponse getShopDetails(String placeId, double x, double y) throws JsonProcessingException {
        ShopApiDto shopApiDto;

        String shopStr = this.callApiByPlaceId(placeId);
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

        String openNow;
        try {
            openNow = shopApiDto.getOpening_hours().getOpen_now();
        } catch (NullPointerException e) {
            openNow = null;
        }

        ShopResponse shopResponse = ShopResponse.builder()
                .place_id(shopApiDto.getPlace_id())
                .name(shopApiDto.getName())
                .formatted_address(shopApiDto.getFormatted_address())
                .formatted_phone_number(shopApiDto.getFormatted_phone_number())
                .x(xTemp)
                .y(yTemp)
                .open_now(openNow)
                .businessDay(businessDay)
                .build();

        if (shopResponse.getX() != null && shopResponse.getY() != null) {
            shopResponse.setDist(x, y);
        } else {
            shopResponse.setDist();
        }

        GoogleShopEntity shopEntity = googleShopRepository.findByPlaceId(shopResponse.getPlace_id());

        if (shopEntity != null) {
            GoogleShopCount shopCount = shopEntity.getShopCount();
            Integer totalRating = shopCount.getTotalRating();
            Integer ratingCount = shopCount.getRatingCount();

            shopResponse.setShopCount(totalRating, ratingCount);
        }

        return shopResponse;
    }

    //todo: 사진 구현
//    @Override
//    public byte[] getShopPhoto(String photoToken) {
//
//        //redirect
//
//
//        byte[] photo = webClient.get().uri(uriBuilder ->
//                uriBuilder.path("/photo")
//                        .queryParam("photo_reference", photoToken)
//                        .queryParam("key", API_KEY)
//                        .queryParam("maxwidth", 400)
//                        .build()
//        ).retrieve().bodyToMono(byte[].class).block();
//
//        return photo;
//
//    }

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

            String openNow;
            try {
                openNow = dto.getOpening_hours().getOpen_now();
            } catch (NullPointerException e) {
                openNow = null;
            }

            ShopQueryResponse shopQueryResponse = ShopQueryResponse.builder()
                    .place_id(dto.getPlace_id())
                    .name(dto.getName())
                    .formatted_address(dto.getFormatted_address())
                    .x(xTemp)
                    .y(yTemp)
                    .open_now(openNow)
                    .build();

            if (shopQueryResponse.getX() != null && shopQueryResponse.getY() != null) {
                shopQueryResponse.setDist(x, y);
            } else {
                shopQueryResponse.setDist();
            }

            shopQueryResponseList.add(shopQueryResponse);
        }

        ShopQueryResponses shopQueryResponses = new ShopQueryResponses(shopQueryDto.getNext_page_token(), shopQueryResponseList);
        return shopQueryResponses;
    }

    private String callApiByPlaceId(String placeId) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("language", "ko")
                        .queryParam("key", API_KEY)
                        .queryParam("fields", "formatted_address,formatted_phone_number,name,geometry/location/lat,geometry/location/lng,types,place_id,opening_hours/open_now,opening_hours/weekday_text,photos")
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    private String callApiByCafeQuery(String query) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/textsearch/json")
                        .queryParam("query", query)
                        .queryParam("key", API_KEY)

                        .queryParam("language", "ko")
                        .queryParam("type", "cafe")
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    private String callApiByRestaurantQuery(String query) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/textsearch/json")
                        .queryParam("query", query)
                        .queryParam("key", API_KEY)

                        //세부 제한
                        .queryParam("language", "ko")
                        .queryParam("type", "restaurant")
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    private String callApiByNextTokenPage(String next_token_page) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/textsearch/json")
                        .queryParam("pagetoken", next_token_page)
                        .queryParam("key", API_KEY)
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return shopStr;
    }

    /**
     * shopDetails 상점 정보
     */
    private ShopApiDto jsonToShopApiDto(String shopStr) throws JsonProcessingException {
        Map<String, Object> map = objectMapper.readValue(shopStr, new TypeReference<HashMap<String, Object>>() {
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

        String resultStr = objectMapper.writeValueAsString(map.get("result"));
        ShopApiDto shopApiDto = objectMapper.readValue(resultStr, ShopApiDto.class);

        return shopApiDto;
    }

    private ShopQueryDto jsonToShopQueryDto(String shopStr) throws JsonProcessingException {
        Map<String, Object> map = objectMapper.readValue(shopStr, new TypeReference<HashMap<String, Object>>() {
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

        System.out.println(shopStr);

        ShopQueryDto shopQueryDto = objectMapper.readValue(shopStr, ShopQueryDto.class);

        return shopQueryDto;
    }

    @Override
    public void saveGoogleShop(String placeId) {
        GoogleShopEntity shopEntity = GoogleShopEntity.builder()
                .placeId(placeId)
                .build();

        this.googleShopRepository.save(shopEntity);
    }
}
