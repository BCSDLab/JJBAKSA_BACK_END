package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.google.dto.api.Prediction;
import com.jjbacsa.jjbacsabackend.google.dto.api.ShopApiDto;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.OpeningHours;
import com.jjbacsa.jjbacsabackend.google.dto.request.AutoCompleteRequest;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.serviceImpl.GoogleShopServiceImpl;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Setter
@Getter
@Builder
class ShopApiResponse {
    String status;
    ShopApiDto result;
}

@Getter
@Builder
class AutoCompleteResponse {
    String status;
    Prediction[] predictions;
}

@ExtendWith(MockitoExtension.class)
public class MockServerTest {
    static GoogleShopService googleShopService;
    static MockWebServer mockWebServer;
    static ObjectMapper objectMapper;

    static GoogleShopRepository googleShopRepository;
    static InternalFollowService internalFollowService;
    static InternalReviewService internalReviewService;
    static InternalScrapService internalScrapService;

    static ShopApiResponse baseResponse;
    static Geometry geometry;


    @BeforeAll
    static void init() throws IOException {
        googleShopRepository = Mockito.mock(GoogleShopRepository.class);
        internalReviewService = Mockito.mock(InternalReviewService.class);
        internalFollowService = Mockito.mock(InternalFollowService.class);
        internalScrapService = Mockito.mock(InternalScrapService.class);

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        googleShopService = new GoogleShopServiceImpl(baseUrl, new ObjectMapper(), "KEY", googleShopRepository, internalFollowService, internalReviewService, internalScrapService);
        objectMapper = new ObjectMapper();

        Geometry.Location location = new Geometry.Location();
        location.setLng(127.0);
        location.setLat(35.0);
        geometry = new Geometry();
        geometry.setLocation(location);
    }

    @BeforeEach
    void initResponse() {
        ShopApiDto shopApiDto = ShopApiDto.builder()
                .placeId("placeId")
                .name("상점")
                .build();

        baseResponse = ShopApiResponse.builder()
                .status("OK")
                .result(shopApiDto)
                .build();
    }

    @AfterAll
    static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void 영업시간_정보_없음() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(baseResponse)).addHeader("Content-Type", "application/json"));

        ShopResponse shopResponse = googleShopService.getShopDetails("placeId");
        Assertions.assertNull(shopResponse.getTodayPeriod());
    }

    @Test
    void 영업시간_정보_제공() throws JsonProcessingException {
        int dayNumber = getTodayWeekTypeNumber();

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow("true");

        OpeningHours.Period.PeriodTime open = new OpeningHours.Period.PeriodTime();
        open.setDay(dayNumber);
        open.setTime("0700");

        OpeningHours.Period.PeriodTime close = new OpeningHours.Period.PeriodTime();
        close.setDay(dayNumber);
        close.setTime("2230");

        OpeningHours.Period period = new OpeningHours.Period();
        period.setOpen(open);
        period.setClose(close);

        openingHours.setPeriods(List.of(period));

        baseResponse.getResult().setOpeningHours(openingHours);
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(baseResponse)));

        ShopResponse shopResponse = googleShopService.getShopDetails("placeId");
        Assertions.assertEquals(7, shopResponse.getTodayPeriod().getOpenTime().getHour());
        Assertions.assertEquals(0, shopResponse.getTodayPeriod().getOpenTime().getMinute());

        Assertions.assertEquals(22, shopResponse.getTodayPeriod().getCloseTime().getHour());
        Assertions.assertEquals(30, shopResponse.getTodayPeriod().getCloseTime().getMinute());
    }

    @Test
    void 상점_사진_정보_없음() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(baseResponse)).addHeader("Content-Type", "application/json"));

        ShopResponse shopResponse = googleShopService.getShopDetails("placeId");
        Assertions.assertTrue(shopResponse.getPhotos().isEmpty());
    }

    @Test
    void 상점_좌표_정보_없음() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(baseResponse)).addHeader("Content-Type", "application/json"));

        ShopResponse shopResponse = googleShopService.getShopDetails("placeId");
        Assertions.assertNull(shopResponse.getCoordinate());
    }

    @Transactional
    @Test
    void 상점_별점_기본형_반환() throws Exception {
        Mockito.when(googleShopRepository.existsByPlaceId("placeId"))
                .thenReturn(false);

        Assertions.assertEquals(0, googleShopService.getShopRate("placeId").getRatingCount());
    }

    @Test
    void 상점_별점_반환() {
        Mockito.when(googleShopRepository.existsByPlaceId("placeId"))
                .thenReturn(true);

        Mockito.when(googleShopRepository.getByPlaceId("placeId"))
                .thenReturn(GoogleShopEntity.builder()
                        .shopCount(GoogleShopCount.builder()
                                .ratingCount(4)
                                .totalRating(1).build())
                        .build()
                );

        Assertions.assertEquals(4, googleShopService.getShopRate("placeId").getRatingCount());
    }

    @Test
    void 가까운_상점_반환() throws Exception {
        GoogleShopEntity googleShopEntity1 = GoogleShopEntity.builder().id(1L).placeId("placeId1").build();
        GoogleShopEntity googleShopEntity2 = GoogleShopEntity.builder().id(2L).placeId("placeId2").build();
        GoogleShopEntity googleShopEntity3 = GoogleShopEntity.builder().id(3L).placeId("placeId3").build();

        Mockito.when(googleShopRepository.findAll())
                .thenReturn(List.of(googleShopEntity1, googleShopEntity2, googleShopEntity3));

        Mockito.when(googleShopRepository.findById(1L)).thenReturn(Optional.of(googleShopEntity1));
        Mockito.when(googleShopRepository.findById(2L)).thenReturn(Optional.of(googleShopEntity2));
        Mockito.when(googleShopRepository.findById(3L)).thenReturn(Optional.of(googleShopEntity3));

        Geometry.Location location = new Geometry.Location();
        location.setLng(127.0);
        location.setLat(35.0);
        Geometry geometry = new Geometry();
        geometry.setLocation(location);

        ShopApiResponse shopApiResponse1 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId1")
                        .geometry(geometry)
                        .build()
                ).build();

        ShopApiResponse shopApiResponse2 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId2")
                        .geometry(geometry)
                        .build()
                ).build();

        ShopApiResponse shopApiResponse3 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId3")
                        .geometry(geometry)
                        .build()
                ).build();

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse1)));
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse2)));
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse3)));

        Assertions.assertEquals(3, googleShopService.getShops(1, 0, 0,
                ShopRequest.builder().lng(127).lat(35).build()).size()
        );
    }

    @Test
    void 친구_리뷰_상점_반환() throws Exception {
        List<UserEntity> userEntities = new ArrayList<>();
        UserEntity followerEntity = UserEntity.builder()
                .id(2L)
                .build();

        userEntities.add(followerEntity);

        Mockito.when(internalFollowService.getFollowers())
                .thenReturn(userEntities);

        Mockito.when(internalReviewService.getReviewShopIdsForUser(followerEntity))
                .thenReturn(List.of(1L, 2L, 3L, 4L));

        GoogleShopEntity googleShopEntity1 = GoogleShopEntity.builder().id(1L).placeId("placeId1").build();
        GoogleShopEntity googleShopEntity2 = GoogleShopEntity.builder().id(2L).placeId("placeId2").build();
        GoogleShopEntity googleShopEntity3 = GoogleShopEntity.builder().id(3L).placeId("placeId3").build();
        GoogleShopEntity googleShopEntity4 = GoogleShopEntity.builder().id(3L).placeId("placeId4").build();

        Mockito.when(googleShopRepository.findById(1L)).thenReturn(Optional.of(googleShopEntity1));
        Mockito.when(googleShopRepository.findById(2L)).thenReturn(Optional.of(googleShopEntity2));
        Mockito.when(googleShopRepository.findById(3L)).thenReturn(Optional.of(googleShopEntity3));
        Mockito.when(googleShopRepository.findById(4L)).thenReturn(Optional.of(googleShopEntity4));

        ShopApiResponse shopApiResponse1 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId1")
                        .geometry(geometry)
                        .build()
                ).build();

        ShopApiResponse shopApiResponse2 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId2")
                        .geometry(geometry)
                        .build()
                ).build();

        ShopApiResponse shopApiResponse3 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId3")
                        .geometry(geometry)
                        .build()
                ).build();

        ShopApiResponse shopApiResponse4 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId4")
                        .geometry(geometry)
                        .build()
                ).build();

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse1)));
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse2)));
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse3)));
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse4)));

        Assertions.assertEquals(4, googleShopService.getShops(0, 1, 0,
                ShopRequest.builder().lng(127).lat(35).build()).size()
        );
    }

    @Test
    void 스크랩_상점_반환() throws Exception {
        Mockito.when(internalScrapService.getShopIdsForUserScrap())
                .thenReturn(List.of(1L));

        GoogleShopEntity googleShopEntity1 = GoogleShopEntity.builder().id(1L).placeId("placeId1").build();
        Mockito.when(googleShopRepository.findById(1L)).thenReturn(Optional.of(googleShopEntity1));

        ShopApiResponse shopApiResponse1 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId1")
                        .geometry(geometry)
                        .build()
                ).build();

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse1)));

        Assertions.assertEquals(1, googleShopService.getShops(0, 0, 1,
                ShopRequest.builder().lng(127).lat(35).build()).size()
        );
    }

    @Test
    void 리뷰_스크랩_상점_중복집계_제거() throws Exception {
        List<UserEntity> userEntities = new ArrayList<>();
        UserEntity followerEntity = UserEntity.builder()
                .id(1L)
                .build();
        userEntities.add(followerEntity);

        Mockito.when(internalFollowService.getFollowers())
                .thenReturn(userEntities);

        Mockito.when(internalReviewService.getReviewShopIdsForUser(followerEntity))
                .thenReturn(List.of(1L));
        Mockito.when(internalScrapService.getShopIdsForUserScrap())
                .thenReturn(List.of(1L));

        GoogleShopEntity googleShopEntity1 = GoogleShopEntity.builder().id(1L).placeId("placeId1").build();
        Mockito.when(googleShopRepository.findById(1L)).thenReturn(Optional.of(googleShopEntity1));

        ShopApiResponse shopApiResponse1 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId1")
                        .geometry(geometry)
                        .build()
                ).build();

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse1)));

        Assertions.assertEquals(1, googleShopService.getShops(0, 1, 1,
                ShopRequest.builder().lng(127).lat(35).build()).size()
        );
    }

    @Test
    void 리뷰_스크랩_상점_중복없이_집계() throws Exception {
        List<UserEntity> userEntities = new ArrayList<>();
        UserEntity followerEntity = UserEntity.builder()
                .id(1L)
                .build();
        userEntities.add(followerEntity);

        Mockito.when(internalFollowService.getFollowers())
                .thenReturn(userEntities);

        Mockito.when(internalReviewService.getReviewShopIdsForUser(followerEntity))
                .thenReturn(List.of(1L));
        Mockito.when(internalScrapService.getShopIdsForUserScrap())
                .thenReturn(List.of(2L));

        GoogleShopEntity googleShopEntity1 = GoogleShopEntity.builder().id(1L).placeId("placeId1").build();
        GoogleShopEntity googleShopEntity2 = GoogleShopEntity.builder().id(2L).placeId("placeId2").build();
        Mockito.when(googleShopRepository.findById(1L)).thenReturn(Optional.of(googleShopEntity1));
        Mockito.when(googleShopRepository.findById(2L)).thenReturn(Optional.of(googleShopEntity2));

        ShopApiResponse shopApiResponse1 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId1")
                        .geometry(geometry)
                        .build()
                ).build();

        ShopApiResponse shopApiResponse2 = ShopApiResponse.builder()
                .status("OK")
                .result(ShopApiDto.builder()
                        .placeId("placeId2")
                        .geometry(geometry)
                        .build()
                ).build();

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse1)));
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(shopApiResponse2)));

        Assertions.assertEquals(2, googleShopService.getShops(0, 1, 1,
                ShopRequest.builder().lng(127).lat(35).build()).size()
        );
    }

    @Test
    void 자동완성_테스트() throws JsonProcessingException {
        Prediction prediction1 = new Prediction();
        Prediction prediction2 = new Prediction();

        String mainText1 = "자동완성1";
        String mainText2 = "자동완성2";
        Prediction.StructuredFormatting structuredFormatting1 = new Prediction.StructuredFormatting();
        structuredFormatting1.setMainText(mainText1);
        Prediction.StructuredFormatting structuredFormatting2 = new Prediction.StructuredFormatting();
        structuredFormatting2.setMainText(mainText2);

        prediction1.setStructuredFormatting(structuredFormatting1);
        prediction2.setStructuredFormatting(structuredFormatting2);

        Prediction[] predictions = new Prediction[]{prediction1, prediction2};
        AutoCompleteResponse autoCompleteResponse = AutoCompleteResponse.builder()
                .status("OK")
                .predictions(predictions)
                .build();

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(autoCompleteResponse)));

        AutoCompleteRequest autoCompleteRequest = AutoCompleteRequest.builder()
                .lng(127.0)
                .lat(35.0)
                .build();
        Assertions.assertEquals(2, googleShopService.getAutoComplete("자동완성", autoCompleteRequest).size());
    }

    private int getTodayWeekTypeNumber() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
