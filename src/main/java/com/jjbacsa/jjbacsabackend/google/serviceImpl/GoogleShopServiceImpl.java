package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.etc.exception.BaseException;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.google.dto.*;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.request.AutoCompleteRequest;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopQueryResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@Service
public class GoogleShopServiceImpl implements GoogleShopService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/place";
    private final String API_KEY;
    private final GoogleShopRepository googleShopRepository;
    private final InternalFollowService followService;
    private final InternalReviewService reviewService;
    private final InternalScrapService scrapService;

    public GoogleShopServiceImpl(ObjectMapper objectMapper, @Value("${external.api.key}") String key, GoogleShopRepository googleShopRepository, InternalFollowService internalFollowService, InternalReviewService internalReviewService, InternalScrapService internalScrapService) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        this.API_KEY = key;
        this.googleShopRepository = googleShopRepository;
        this.followService = internalFollowService;
        this.reviewService = internalReviewService;
        this.scrapService = internalScrapService;

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
    public ShopQueryResponses searchShopQuery(String query, ShopRequest shopRequest) throws JsonProcessingException {
        String shopStr = null;

        shopStr = this.callApiByQuery(query, shopRequest);

        ShopQueryDto shopQueryDto = this.jsonToShopQueryDto(shopStr);
        ShopQueryResponses shopQueryResponses = this.queryDtoToQueryResponses(shopQueryDto, shopRequest);

        return shopQueryResponses;
    }

    @Override
    public ShopQueryResponses searchShopQueryNext(String pageToken, ShopRequest shopRequest) throws JsonProcessingException {
        ShopQueryDto shopQueryDto;

        String shopStr = this.callApiByQuery(pageToken);
        shopQueryDto = this.jsonToShopQueryDto(shopStr);

        ShopQueryResponses shopQueryResponses = this.queryDtoToQueryResponses(shopQueryDto, shopRequest);
        return shopQueryResponses;
    }

    @Transactional(readOnly = true)
    @Override
    public ShopResponse getShopDetails(String placeId) throws Exception {
        String shopStr = this.callGoogleApiByPlaceId(placeId);
        ShopApiDto shopApiDto = this.jsonToShopApiDto(shopStr);

        String businessDay;
        String todayBusinessHour;
        try {
            //오늘 날짜 가져오기
            LocalDate today = LocalDate.now();
            int dayOfWeek = today.getDayOfWeek().getValue() - 1;

            JSONArray jsonArray = new JSONArray();
            for (String weekday : shopApiDto.getOpeningHours().getWeekdayText()) {
                jsonArray.add(weekday);
            }
            businessDay = jsonArray.toJSONString();

            todayBusinessHour = shopApiDto.getOpeningHours().getWeekdayText().get(dayOfWeek);
            todayBusinessHour = todayBusinessHour.substring(5);

        } catch (NullPointerException e) {
            businessDay = null;
            todayBusinessHour = null;
        }

        Boolean openNow;
        try {
            openNow = (shopApiDto.getOpeningHours().getOpenNow() == "true") ? true : false;
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
                .placeId(shopApiDto.getPlaceId())
                .name(shopApiDto.getName())
                .formattedAddress(shopApiDto.getFormattedAddress())
                .formattedPhoneNumber(shopApiDto.getFormattedPhoneNumber())
                .lat(shopApiDto.getGeometry().getLocation().getLat())
                .lng(shopApiDto.getGeometry().getLocation().getLng())
                .openNow(openNow)
                .photoToken(token)
                .businessDay(businessDay)
                .category(category.name())
                .todayBusinessHour(todayBusinessHour)
                .build();

        Optional<GoogleShopEntity> shop = googleShopRepository.findByPlaceId(shopResponse.getPlaceId());
        boolean isScrap = false;
        if (shop.isPresent()) {
            GoogleShopCount shopCount = shop.get().getShopCount();
            shopResponse.setShopCount(shopCount.getTotalRating(), shopCount.getRatingCount());

            isScrap = scrapService.isUserScrapShop(shop.get().getId());
        }

        shopResponse.setIsScrap(isScrap);

        return shopResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SimpleShopDto> getShops(Integer nearBy, Integer friend, Integer scrap, ShopRequest shopRequest) throws Exception {

        List<Long> shopIds = getShopId(nearBy, friend, scrap);
        List<String> placeIDs = getPlaceIds(shopIds);

        // 2000m(2km) 이내
        List<SimpleShopDto> simpleShopDtos = this.callApiByPlaceIdsNonBlocking(placeIDs);

        int failCnt = 0;
        List<SimpleShopDto> resultSimpleShopDtos = new ArrayList<>();
        for (SimpleShopDto dto : simpleShopDtos) {
            try {
                Double dist = getMeter(dto.getGeometry(), shopRequest);

                if (dist <= 2000) {
                    resultSimpleShopDtos.add(dto);
                }
            } catch (Exception e) {
                failCnt++;
            }

            if (failCnt >= simpleShopDtos.size() / 2) {
                throw new ApiException(ErrorMessage.OVER_QUERY_LIMIT_EXCEPTION);
            }
        }

        return resultSimpleShopDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public ShopResponse getShop(String placeId) throws Exception {
        GoogleShopEntity googleShopEntity = googleShopRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION));

        return this.getShopDetails(googleShopEntity.getPlaceId());
    }

    @Override
    public List<String> getAutoComplete(String query, AutoCompleteRequest autoCompleteRequest) throws JsonProcessingException {
        List<String> autoCompleteResult = new ArrayList<>();

        String autoCompleteStr = this.callGoogleAutoComplete(query, autoCompleteRequest);

        Map<String, Object> map = null;
        try {
            map = this.checkApiReturn(autoCompleteStr);
        } catch (ApiException e) {
            if (e.getErrorMessage().equals(ErrorMessage.ZERO_RESULTS_EXCEPTION.getErrorMessage())) {
                return autoCompleteResult;
            }
        }

        String reusltStr = objectMapper.writeValueAsString(map.get("predictions"));
        Prediction[] autoCompleteApiDto = objectMapper.readValue(reusltStr, Prediction[].class);

        for (Prediction p : autoCompleteApiDto) {
            String pStr = p.getStructuredFormatting().getMainText();

            if (!autoCompleteResult.contains(pStr)) {
                autoCompleteResult.add(pStr);
            }
        }

        return autoCompleteResult;
    }

    private Double getMeter(Geometry geometry, ShopRequest shopRequest) {

        if (geometry.getLocation().getLat() == null || geometry.getLocation().getLng() == null)
            return null;

        double userLat = shopRequest.getLat();
        double userLng = shopRequest.getLng();

        double shopLat = geometry.getLocation().getLat();
        double shopLng = geometry.getLocation().getLng();

        double theta = userLng - shopLng;
        double dist = Math.sin(deg2rad((userLat))) * Math.sin(deg2rad(shopLat))
                + Math.cos(deg2rad(userLat)) * Math.cos(deg2rad(shopLat)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist *= 60 * 1.1515 * 1609.344; //meter

        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private List<String> getPlaceIds(List<Long> shopIds) {
        List<String> placeIds = new ArrayList<>();

        shopIds.stream()
                .map(shopId -> googleShopRepository.findById(shopId))
                .map(optionalGoogleShop -> optionalGoogleShop.orElseThrow(() -> new BaseException(ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION)))
                .forEach(googleShop -> placeIds.add(googleShop.getPlaceId()));

        return placeIds;
    }

    //필터에 따라 상점 id 가져오는 메소드
    private List<Long> getShopId(Integer nearBy, Integer friend, Integer scrap) throws Exception {
        if (nearBy == 1) {
            return googleShopRepository.findAll()
                    .stream()
                    .map(GoogleShopEntity::getId)
                    .collect(Collectors.toList());
        }

        List<Long> shopIds = new ArrayList<>();

        if (friend == 1) {
            List<Long> friends = followService.getFollowers();

            //todo: 후에 리뷰 변경되면 변경
            friends.stream()
                    .map(f -> reviewService.getReviewIdsForUser(f))
                    .forEach(ids -> shopIds.addAll(ids));
        }

        shopIds.sort(Comparator.naturalOrder());

        if (scrap == 1) {
            List<Long> ids = scrapService.getShopIdsForUserScrap();

            for (Long id : ids) {
                int insertionIndex = Collections.binarySearch(shopIds, id);

                if (insertionIndex < 0) {
                    insertionIndex = -(insertionIndex + 1);
                    shopIds.add(insertionIndex, id);
                }
            }
        }

        return shopIds;
    }

    private ShopQueryResponses queryDtoToQueryResponses(ShopQueryDto shopQueryDto, ShopRequest shopRequest) {
        List<ShopQueryResponse> shopQueryResponseList = new ArrayList<>();

        for (ShopQueryApiDto dto : shopQueryDto.getResults()) {
            Boolean openNow;
            try {
                openNow = (dto.getOpeningHours().getOpenNow().equals("true")) ? true : false;
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

            Double distFromUser = this.getMeter(dto.getGeometry(), shopRequest);

            ShopQueryResponse shopQueryResponse = ShopQueryResponse.builder()
                    .placeId(dto.getPlaceId())
                    .name(dto.getName())
                    .formattedAddress(dto.getFormattedAddress())
                    .lat(dto.getGeometry().getLocation().getLat())
                    .lng(dto.getGeometry().getLocation().getLng())
                    .openNow(openNow)
                    .photoToken(token)
                    .category(category.name())
                    .dist(distFromUser)
                    .build();

            Optional<GoogleShopEntity> shop = googleShopRepository.findByPlaceId(dto.getPlaceId());

            if (shop.isPresent()) {
                GoogleShopCount shopCount = shop.get().getShopCount();
                shopQueryResponse.setShopCount(shopCount.getTotalRating(), shopCount.getRatingCount());
            }

            shopQueryResponseList.add(shopQueryResponse);
        }

        ShopQueryResponses shopQueryResponses = new ShopQueryResponses(shopQueryDto.getNextPageToken(), shopQueryResponseList);
        return shopQueryResponses;
    }

    /**
     * 여러 place_ids에서 간단하게 상점 정보를 받아오는 메소드
     */
    private List<SimpleShopDto> callApiByPlaceIdsNonBlocking(List<String> placeIds) throws JsonProcessingException {
        List<Mono<String>> monos = new ArrayList<>();

        for (String id : placeIds) {
            Mono<String> shopStr;

            //todo: queryParam 설명에는 fields list 된다고 되어있는데 실제로 안됨 확인좀(string으로 넣어주면 됨)
            shopStr = webClient.get().uri(uriBuilder ->
                            uriBuilder.path("/details/json")
                                    .queryParam("place_id", id)
                                    .queryParam("language", "ko")
                                    .queryParam("key", API_KEY)
                                    .queryParam("fields", "geometry/location/lng,geometry/location/lat,place_id,name")
                                    .build()
                    )
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
    private String callApiByQuery(String query, ShopRequest shopRequest) {

        String locationQuery = String.valueOf(shopRequest.getLat()) + ", " + String.valueOf(shopRequest.getLng());

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
    private String callGoogleApiByPlaceId(String placeId) {
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
     * 자동완성 요청을 위한 내부 메소드
     */
    private String callGoogleAutoComplete(String query, AutoCompleteRequest autoCompleteRequest) {
        String locationQuery = String.valueOf(autoCompleteRequest.getLat()) + ", " + String.valueOf(autoCompleteRequest.getLng());

        String autoCompleteStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/autocomplete/json")
                        .queryParam("input", query)
                        .queryParam("components", "country:kr")
                        .queryParam("language", "ko")
                        .queryParam("location", locationQuery)
                        .queryParam("radius", 500)
                        .queryParam("types", "restaurant|cafe")
                        .queryParam("key", API_KEY)
                        .build()
        ).retrieve().bodyToMono(String.class).block();

        return autoCompleteStr;
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

        try {
            this.checkApiReturn(shopStr);
        } catch (ApiException e) {
            if (e.getErrorMessage().equals(ErrorMessage.ZERO_RESULTS_EXCEPTION.getErrorMessage())) {
                ArrayList<ShopQueryApiDto> shopQueryApiDtos = new ArrayList<>();

                return ShopQueryDto.builder()
                        .nextPageToken(null)
                        .results(shopQueryApiDtos)
                        .build();
            }
        }

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