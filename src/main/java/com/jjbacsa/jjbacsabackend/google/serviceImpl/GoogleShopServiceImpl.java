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
import com.jjbacsa.jjbacsabackend.google.dto.response.*;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
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

    private final String[] placeDetailsField = {"formatted_address", "formatted_phone_number", "name", "geometry/location/lat", "geometry/location/lng", "types", "place_id", "opening_hours/open_now", "opening_hours/weekday_text", "photos/photo_reference"};
    private final String[] pinFields = {"name", "types", "place_id", "photos/photo_reference"};
    private final String[] simpleFields = {"geometry/location/lng", "geometry/location/lat", "place_id", "name", "photos/photo_reference"};

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
    public ShopResponse getShopDetails(String placeId, boolean isDetail) throws Exception {
        ShopResponse shopResponse;

        String requestField;
        if (isDetail) {
            requestField = toFieldString(placeDetailsField);
        } else {
            requestField = toFieldString(pinFields);
        }

        String shopStr = this.callGoogleApi(placeId, requestField);
        ShopApiDto shopApiDto = this.jsonToShopApiDto(shopStr);

        Category category = getCategory(shopApiDto.getTypes());
        List<String> photoTokens = new ArrayList<>();
        try {
            int maxRange = shopApiDto.getPhotos().size() >= 10 ? 10 : shopApiDto.getPhotos().size();

            for (int p = 0; p < maxRange; p++) {
                photoTokens.add(getPhotoUrl(shopApiDto.getPhotos().get(p).getPhotoReference()));
            }

        } catch (NullPointerException e) {
            photoTokens = null;
        }

        if (isDetail) {
            String businessDay;
            String todayBusinessHour;
            try {
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

            shopResponse = ShopResponse.builder()
                    .placeId(shopApiDto.getPlaceId())
                    .name(shopApiDto.getName())
                    .formattedAddress(shopApiDto.getFormattedAddress())
                    .formattedPhoneNumber(shopApiDto.getFormattedPhoneNumber())
                    .lat(shopApiDto.getGeometry().getLocation().getLat())
                    .lng(shopApiDto.getGeometry().getLocation().getLng())
                    .openNow(openNow)
                    .photos(photoTokens)
                    .businessDay(businessDay)
                    .category(category.name())
                    .todayBusinessHour(todayBusinessHour)
                    .build();
        } else {
            shopResponse = ShopResponse.builder()
                    .placeId(shopApiDto.getPlaceId())
                    .name(shopApiDto.getName())
                    .category(category.name())
                    .photos(photoTokens)
                    .build();
        }

        Optional<GoogleShopEntity> shop = googleShopRepository.findByPlaceId(shopResponse.getPlaceId());
        boolean isScrap = false;
        if (shop.isPresent()) {
            GoogleShopCount shopCount = shop.get().getShopCount();
            shopResponse.setShopCount(shopCount.getTotalRating(), shopCount.getRatingCount());
            shopResponse.setShopId(shop.get().getId());
            isScrap = scrapService.isUserScrapShop(shop.get());
        }

        shopResponse.setIsScrap(isScrap);

        return shopResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShopSimpleResponse> getShops(Integer nearBy, Integer friend, Integer scrap, ShopRequest shopRequest) throws Exception {

        List<Long> shopIds = getShopId(nearBy, friend, scrap);
        List<String> placeIDs = getPlaceIds(shopIds);

        // 2000m(2km) 이내
        List<ShopSimpleResponse> simpleShopDtos = this.callApiByPlaceIdsNonBlocking(placeIDs);

        int failCnt = 0;
        List<ShopSimpleResponse> resultSimpleShopDtos = new ArrayList<>();
        for (ShopSimpleResponse dto : simpleShopDtos) {
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

    @Override
    public ShopScrapResponse getShopScrap(String placeId, Long scrapId) throws JsonProcessingException {

        String requestField=toFieldString(pinFields);
        String shopStr=this.callGoogleApi(placeId, requestField);
        ShopApiDto shopApiDto = this.jsonToShopApiDto(shopStr);
        Category category = getCategory(shopApiDto.getTypes());
        String photoToken;

        try{
            photoToken=getPhotoUrl(shopApiDto.getPhotos().get(0).getPhotoReference());
        } catch (NullPointerException e){
            photoToken=null;
        }

        ShopScrapResponse shopScrapResponse=ShopScrapResponse.builder()
                .placeId(shopApiDto.getPlaceId())
                .name(shopApiDto.getName())
                .category(category.name())
                .photo(photoToken)
                .scrapId(scrapId)
                .build();

        Optional<GoogleShopEntity> shop = googleShopRepository.findByPlaceId(shopScrapResponse.getPlaceId());
        if (shop.isPresent()){
            GoogleShopCount shopCount = shop.get().getShopCount();
            shopScrapResponse.setShopCount(shopCount.getTotalRating(), shopCount.getRatingCount());
        }

        return shopScrapResponse;
    }

    private String toFieldString(String[] fields) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String field : fields) {
            stringBuilder.append(field);
            stringBuilder.append(",");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
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

        List<Long> shopIds = new LinkedList<>();

        if (friend == 1) {
            List<UserEntity> friends = followService.getFollowers();

            friends.stream()
                    .map(f -> reviewService.getReviewIdsForUser(f))
                    .forEach(ids -> addNonDuplication(ids, shopIds));
        }

        shopIds.sort(Comparator.naturalOrder());

        if (scrap == 1) {
            List<Long> ids = scrapService.getShopIdsForUserScrap();
            addNonDuplication(ids, shopIds);
        }

        return shopIds;
    }

    private void addNonDuplication(List<Long> ids, List<Long> resultIds) {
        for (Long id : ids) {
            int insertionIndex = Collections.binarySearch(resultIds, id);

            if (insertionIndex < 0) {
                insertionIndex = -(insertionIndex + 1);
                resultIds.add(insertionIndex, id);
            }
        }
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
                token = getPhotoUrl(dto.getPhotos().get(0).getPhotoReference());
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
    private List<ShopSimpleResponse> callApiByPlaceIdsNonBlocking(List<String> placeIds) throws JsonProcessingException {
        List<Mono<String>> monos = new ArrayList<>();

        for (String id : placeIds) {
            Mono<String> shopStr;

            shopStr = webClient.get().uri(uriBuilder ->
                            uriBuilder.path("/details/json")
                                    .queryParam("place_id", id)
                                    .queryParam("language", "ko")
                                    .queryParam("key", API_KEY)
                                    .queryParam("fields", toFieldString(simpleFields))
                                    .build()
                    )
                    .retrieve().bodyToMono(String.class);

            monos.add(shopStr);
        }

        Function<Object[], List> combinator = monoList -> Arrays.stream(monoList).collect(Collectors.toList());
        List<String> results = Mono.zip(monos, combinator).block();

        if (results == null)
            results = new ArrayList<>();

        List<ShopSimpleResponse> simpleShopDtos = new ArrayList<>();
        int continualException = 0;
        for (String result : results) {
            if (continualException > 5) {
                throw new ApiException(ErrorMessage.CONTINUAL_API_EXCEPTION);
            }
            try {
                continualException = 0;
                SimpleShopDto simpleShopDto = this.jsonToSimpleShopDto(result);

                String photoToken;
                try {
                    photoToken = getPhotoUrl(simpleShopDto.getPhoto().get(0).getPhotoReference());
                } catch (NullPointerException e) {
                    photoToken = null;
                }

                ShopSimpleResponse shopSimpleResponse = ShopSimpleResponse.builder()
                        .placeId(simpleShopDto.getPlaceId())
                        .name(simpleShopDto.getName())
                        .geometry(simpleShopDto.getGeometry())
                        .photo(photoToken)
                        .build();

                simpleShopDtos.add(shopSimpleResponse);
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
    private String callGoogleApi(String placeId, String fieldStr) {
        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("language", "ko")
                        .queryParam("key", API_KEY)
                        .queryParam("fields", fieldStr)
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

    private String getPhotoUrl(String photoToken) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/photo")
                .queryParam("photo_reference", photoToken)
                .queryParam("key", API_KEY)
                .queryParam("maxwidth", 400)
                .queryParam("maxheight", 400)
                .build();

        return uri.toUriString();
    }
}

enum Category {
    cafe, restaurant;
}
