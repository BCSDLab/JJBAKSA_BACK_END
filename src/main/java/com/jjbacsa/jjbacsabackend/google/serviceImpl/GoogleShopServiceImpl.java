package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.WeekType;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.etc.exception.BaseException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.google.dto.Category;
import com.jjbacsa.jjbacsabackend.google.dto.api.*;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.OpeningHours;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Photo;
import com.jjbacsa.jjbacsabackend.google.dto.request.AutoCompleteRequest;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.*;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
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
    private final String BASE_URL;
    private final String API_KEY;
    private final GoogleShopRepository googleShopRepository;
    private final InternalFollowService followService;
    private final InternalReviewService reviewService;
    private final InternalScrapService scrapService;

    private final String[] placeDetailsFields = {"formatted_address", "formatted_phone_number", "name", "geometry/location/lat", "geometry/location/lng", "types", "place_id", "opening_hours/open_now", "opening_hours/weekday_text", "opening_hours/periods", "photos/photo_reference"};
    private final String[] pinFields = {"name", "types", "place_id", "photos/photo_reference"};
    private final String[] simpleFields = {"geometry/location/lng", "geometry/location/lat", "place_id", "name", "photos/photo_reference"};
    private final String[] scrapFields = {"name", "types", "place_id", "photos/photo_reference", "formatted_address"};
    private final String[] addressLevels = {"읍", "면", "동", "가", "로", "길"};
    private final String[] shopExistField = {"place_id"};

    public GoogleShopServiceImpl(@Value("${external.api.url}") String baseUrl, ObjectMapper objectMapper, @Value("${external.api.key}") String key, GoogleShopRepository googleShopRepository, InternalFollowService internalFollowService, InternalReviewService internalReviewService, InternalScrapService internalScrapService) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        this.API_KEY = key;
        this.googleShopRepository = googleShopRepository;
        this.followService = internalFollowService;
        this.reviewService = internalReviewService;
        this.scrapService = internalScrapService;

        this.BASE_URL = baseUrl;

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
    public ShopQueryResponses searchShopQuery(String query, ShopRequest shopRequest, Category category) throws JsonProcessingException {
        String shopStr = this.callApiByQuery(query, shopRequest, category);
        ShopQueryDto shopQueryDto = this.jsonToShopQueryDto(shopStr);

        return queryDtoToQueryResponses(shopQueryDto, shopRequest);
    }

    @Override
    public ShopQueryResponses searchShopQueryNext(String pageToken, ShopRequest shopRequest) throws JsonProcessingException {
        String shopStr = this.callApiByQuery(pageToken);
        ShopQueryDto shopQueryDto = this.jsonToShopQueryDto(shopStr);

        return queryDtoToQueryResponses(shopQueryDto, shopRequest);
    }

    @Override
    public ShopResponse getShopDetails(String placeId) throws JsonProcessingException {
        String requestField = toFieldString(placeDetailsFields);

        String shopStr = this.callGoogleApi(placeId, requestField);
        ShopApiDto shopApiDto = this.jsonToShopApiDto(shopStr);

        Category category = getCategory(shopApiDto.getTypes());
        List<String> photoTokens = getPhotoTokens(shopApiDto);
        TodayPeriod todayPeriod = getPeriod(shopApiDto);

        return ShopResponse.builder()
                .placeId(shopApiDto.getPlaceId())
                .name(shopApiDto.getName())
                .formattedAddress(shopApiDto.getFormattedAddress())
                .formattedPhoneNumber(shopApiDto.getFormattedPhoneNumber())
                .coordinate(Coordinate.from(shopApiDto.getGeometry()))
                .photos(photoTokens)
                .category(category.name())
                .todayPeriod(todayPeriod)
                .build();
    }

    @Override
    public ShopPinResponse getPinShop(String placeId) throws JsonProcessingException {
        String requestField = toFieldString(pinFields);

        String shopStr = this.callGoogleApi(placeId, requestField);
        ShopApiDto shopApiDto = this.jsonToShopApiDto(shopStr);

        Category category = getCategory(shopApiDto.getTypes());
        List<String> photoTokens = getPhotoTokens(shopApiDto);

        return ShopPinResponse.builder()
                .placeId(shopApiDto.getPlaceId())
                .name(shopApiDto.getName())
                .category(category.name())
                .photos(photoTokens)
                .build();
    }

    private List<String> getPhotoTokens(ShopApiDto shopApiDto) {
        try {
            List<String> photos = new ArrayList<>();
            int maxRange = shopApiDto.getPhotos().size() >= 10 ? 10 : shopApiDto.getPhotos().size();

            for (int p = 0; p < maxRange; p++) {
                photos.add(getPhotoUrl(shopApiDto.getPhotos().get(p).getPhotoReference()));
            }

            return photos;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ShopSimpleScrapResponse getSimpleShopScrap(String placeId) throws Exception {
        GoogleShopEntity shopEntity = googleShopRepository.getByPlaceId(placeId);
        Long scrapId = scrapService.getUserScrapShop(shopEntity);

        return ShopSimpleScrapResponse.createScrappedResponse(scrapId);
    }

    @Override
    public boolean isShopExist(String placeId) {
        String checkedPlaceId;

        try {
            String shopExistStr = callGoogleApi(placeId, toFieldString(shopExistField));
            ShopApiDto shopApiDto = jsonToShopApiDto(shopExistStr);
            checkedPlaceId = shopApiDto.getPlaceId();
        } catch (Exception e) {
            return false;
        }

        if (checkedPlaceId == null) {
            return false;
        }

        return true;
    }

    private TodayPeriod getPeriod(ShopApiDto shopApiDto) {
        List<OpeningHours.Period> apiPeriods;

        try {
            apiPeriods = shopApiDto.getOpeningHours().getPeriods();
        } catch (NullPointerException e) {
            return null;
        }

        WeekType todayWeekType = getTodayWeekType();
        TodayPeriod todayPeriod = null;
        for (OpeningHours.Period apiPeriod : apiPeriods) {
            WeekType weekType = WeekType.getWeekType(apiPeriod.getOpen().getDay());

            if (weekType != todayWeekType) {
                continue;
            }

            todayPeriod = TodayPeriod.createPeriod(apiPeriod);
        }

        return todayPeriod;
    }

    private WeekType getTodayWeekType() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);

        return WeekType.getWeekTypeByCalender(dayOfWeekNumber);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShopSimpleResponse> getShops(Integer nearBy, Integer friend, Integer scrap, ShopRequest shopRequest) throws Exception {

        List<Long> shopIds = getShopId(nearBy, friend, scrap);

//        int shopsSize = shopIds.size();
//        if (shopsSize >= 5) {
//            shopIds = shopIds.subList(shopsSize - 5, shopsSize);
//        }

        List<String> placeIDs = getPlaceIds(shopIds);

        // 2000m(2km) 이내
        List<ShopSimpleResponse> simpleShopDtos = this.callApiByPlaceIdsNonBlocking(placeIDs);

        int failCnt = 0;
        List<ShopSimpleResponse> resultSimpleShopDtos = new ArrayList<>();
        for (ShopSimpleResponse dto : simpleShopDtos) {
            try {
                Double dist = getMeter(dto.getCoordinate(), shopRequest);
                resultSimpleShopDtos.add(dto);
            } catch (Exception e) {
                failCnt++;
            }

            if (failCnt >= simpleShopDtos.size() / 2 && failCnt != 0) {
                throw new ApiException(ErrorMessage.OVER_QUERY_LIMIT_EXCEPTION);
            }
        }

        return resultSimpleShopDtos;
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

    @Override
    public ShopScrapResponse getShopScrap(String placeId) throws JsonProcessingException {
        String requestField = toFieldString(scrapFields);
        String shopStr = this.callGoogleApi(placeId, requestField);
        ShopApiDto shopApiDto = this.jsonToShopApiDto(shopStr);

        Category category = getCategory(shopApiDto.getTypes());
        String photoToken = getSinglePhotoToken(shopApiDto.getPhotos());

        ShopRateResponse shopRateResponse = getShopRate(placeId);

        return ShopScrapResponse.builder()
                .placeId(shopApiDto.getPlaceId())
                .name(shopApiDto.getName())
                .category(category.name())
                .photo(photoToken)
                .address(shopApiDto.getFormattedAddress())
                .rate(shopRateResponse)
                .build();
    }


    @Override
    public ShopRateResponse getShopRate(String placeId) {
        if (!googleShopRepository.existsByPlaceId(placeId)) {
            return ShopRateResponse.createDefaultRateResponse();
        }

        GoogleShopEntity shopEntity = googleShopRepository.getByPlaceId(placeId);
        GoogleShopCount countEntity = shopEntity.getShopCount();

        return ShopRateResponse.from(countEntity);
    }

    private String toFieldString(String[] fields) {
        StringJoiner sj = new StringJoiner(",");

        for (String field : fields) {
            sj.add(field);
        }

        return sj.toString();
    }

    private Double getMeter(Coordinate coordinate, ShopRequest shopRequest) {
        if(coordinate == null){
            return null;
        }

        double userLat = shopRequest.getLat();
        double userLng = shopRequest.getLng();

        double shopLat = coordinate.getLat();
        double shopLng = coordinate.getLng();

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
                    .map(f -> reviewService.getReviewShopIdsForUser(f))
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
            Boolean openNow = getOpenNow(dto.getOpeningHours());
            String token = getSinglePhotoToken(dto.getPhotos());
            Category category = getCategory(dto.getTypes());
            Coordinate coordinate = Coordinate.from(dto.getGeometry());
            Double dist = getMeter(coordinate, shopRequest);
            String simpleFormattedAddress = formattedAddressFormatting(dto.getFormattedAddress());

            ShopQueryResponse shopQueryResponse = ShopQueryResponse.builder()
                    .placeId(dto.getPlaceId())
                    .name(dto.getName())
                    .formattedAddress(dto.getFormattedAddress())
                    .simpleFormattedAddress(simpleFormattedAddress)
                    .coordinate(coordinate)
                    .openNow(openNow)
                    .photoToken(token)
                    .category(category.name())
                    .dist(dist)
                    .build();

            shopQueryResponseList.add(shopQueryResponse);
        }

        ShopQueryResponses shopQueryResponses = new ShopQueryResponses(shopQueryDto.getNextPageToken(), shopQueryResponseList);
        return shopQueryResponses;
    }

    private Boolean getOpenNow(OpeningHours openingHours) {
        try {
            return openingHours.getOpenNow().equals("true") ? true : false;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private String getSinglePhotoToken(List<Photo> photos) {
        try {
            return getPhotoUrl(photos.get(0).getPhotoReference());
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 상점 미리보기에서 제공하는 것과 같이 법정구역상 ~동 ~읍 ~면 / 행정구역상 ~구로
     * 상점 위치정보를 반환하기 위한 메소드
     */
    private String formattedAddressFormatting(String address) {
        String[] addressArr = address.split(" ");

        int formattedIdx = -1;
        for (int i = 0; i < addressArr.length; i++) {
            String addressBlock = addressArr[i];

            String lastWord = addressBlock.substring(addressBlock.length() - 1);
            for (String addressLevel : addressLevels) {
                if (addressLevel.equals(lastWord)) {
                    formattedIdx = i;
                    break;
                }
            }

            if (formattedIdx >= 0) {
                break;
            }
        }

        if (formattedIdx < 0) {
            return address;
        }

        StringJoiner sj = new StringJoiner(" ");
        for (int i = 0; i <= formattedIdx; i++) {
            sj.add(addressArr[i]);
        }

        return sj.toString();
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
                    photoToken = getPhotoUrl(simpleShopDto.getPhotos().get(0).getPhotoReference());
                } catch (NullPointerException e) {
                    photoToken = null;
                }

                ShopSimpleResponse shopSimpleResponse = ShopSimpleResponse.builder()
                        .placeId(simpleShopDto.getPlaceId())
                        .name(simpleShopDto.getName())
                        .coordinate(Coordinate.from(simpleShopDto.getGeometry()))
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
    private String callApiByQuery(String query, ShopRequest shopRequest, Category category) {
        String locationQuery = String.valueOf(shopRequest.getLat()) + ", " + String.valueOf(shopRequest.getLng());

        String shopStr = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/textsearch/json")
                        .queryParam("query", query)
                        .queryParam("key", API_KEY)
                        .queryParam("language", "ko")
                        .queryParam("type", category.name())
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
        if(types == null){
            return Category.restaurant;
        }

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

