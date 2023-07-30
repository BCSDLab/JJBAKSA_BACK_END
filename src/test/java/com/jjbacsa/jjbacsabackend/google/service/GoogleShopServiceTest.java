package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.google.dto.request.AutoCompleteRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopSimpleResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapRepository;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@Slf4j
public class GoogleShopServiceTest {
    private final GoogleShopService googleShopService;
    private final GoogleShopRepository googleShopRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserService userService;
    private final ScrapRepository scrapRepository;
    private final ReviewRepository reviewRepository;

    private static UserEntity user;
    private static UserEntity following1; //user가 팔로우하는 사람
    private static UserEntity following2;

    private static GoogleShopEntity googleShop1;
    private static GoogleShopEntity googleShop2;
    private static GoogleShopEntity googleShop3;
    private static GoogleShopEntity googleShop4;
    private static ShopRequest shopRequest;
    String place_id = "ChIJx44qfNaYfDURtM0hCeeC7N4";

    @BeforeEach
    public void init() {
        user = UserEntity.builder()
                .account("testuser")
                .password("password")
                .email("test@google.com")
                .nickname("testuser")
                .userType(UserType.NORMAL)
                .build();

        following1 = UserEntity.builder()
                .account("followinguser1")
                .password("password")
                .email("test@google.com")
                .nickname("followinguser1")
                .userType(UserType.NORMAL)
                .build();

        following2 = UserEntity.builder()
                .account("followinguser2")
                .password("password")
                .email("test@google.com")
                .nickname("followinguser2")
                .userType(UserType.NORMAL)
                .build();

        googleShop1 = GoogleShopEntity.builder()
                .placeId("ChIJQUz778WefDUR7jbncRiF35g").build();
        googleShop2 = GoogleShopEntity.builder()
                .placeId("ChIJmTX1r9uefDUR3wzRWTyHRHo").build();
        googleShop3 = GoogleShopEntity.builder()
                .placeId("ChIJO41ElB-HfDURPvSgus_tqPM").build();
        googleShop4 = GoogleShopEntity.builder()
                .placeId("ChIJ05XTtjGffDURllyJE5KLa90").build();

        shopRequest = ShopRequest.builder()
                .lat(37.5340278)
                .lng(126.9019901)
                .build();
    }

    @Test
    public void 쿼리_다중검색() throws JsonProcessingException {
        String test = "할리스";
        ShopQueryResponses shopQueryResponses = googleShopService.searchShopQuery(test, shopRequest);
        Assertions.assertNotEquals(shopQueryResponses.getShopQueryResponseList().size(), 0);
    }

    @Test
    public void 상점_상세정보() throws Exception {
        ShopResponse shopResponse = googleShopService.getShopDetails(place_id, true);
        Assertions.assertNotEquals(shopResponse.getLat(), null);
        Assertions.assertNotEquals(shopResponse.getLng(), null);
    }

    @Transactional
    @Test
    public void 상점_메인페이지_가까운필터() throws Exception {
        saveAllShops();

        List<ShopSimpleResponse> shops = googleShopService.getShops(1, 0, 0, shopRequest);
        Assertions.assertEquals(4, shops.size());

        shops.stream().forEach(s -> {
            Assertions.assertEquals(s.getPlaceId(), googleShopRepository.findByPlaceId(s.getPlaceId()).get().getPlaceId());
        });

    }

    //todo: 필터 테스트
    @Transactional
    @Test
    public void 상점_메인페이지_친구필터() throws Exception {
        //user 회원가입
        userRepository.save(user);

        //user 로그인
        this.tempLoginForTest(user);

        //팔로우 할 친구들 저장
        userRepository.save(following1);
        userRepository.save(following2);

        //팔로우
        FollowEntity follow1 = FollowEntity.builder()
                .user(user).follower(following1).build();
        FollowEntity follow2 = FollowEntity.builder()
                .user(user).follower(following2).build();

        followRepository.save(follow1);
        followRepository.save(follow2);

        this.saveAllShops();

        //팔로우한 친구가 리뷰 작성
        ReviewEntity reviewEntity1 = ReviewEntity.builder()
                .shop(googleShop1).writer(following1).content("1").rate(2).build();
        ReviewEntity reviewEntity2 = ReviewEntity.builder()
                .shop(googleShop2).writer(following1).content("2").rate(2).build();
        ReviewEntity reviewEntity3 = ReviewEntity.builder()
                .shop(googleShop2).writer(following2).content("3").rate(2).build();

        reviewRepository.save(reviewEntity1);
        reviewRepository.save(reviewEntity2);
        reviewRepository.save(reviewEntity3);

//        List<ShopSimpleResponse> results=this.googleShopService.getShops(0,0,0,shopRequest);
//        Assertions.assertEquals(results.size(),0);

        List<ShopSimpleResponse> results2 = this.googleShopService.getShops(0, 1, 0, shopRequest);
        Assertions.assertEquals(2, results2.size());
    }

    @Transactional
    @Test
    public void 상점_메인페이지_북마크필터() throws Exception {
        //user 회원 가입
        userRepository.save(user);

        //임시 로그인
        tempLoginForTest(user);

        //상점 저장
        saveAllShops();

        //북마크 추가
        ScrapEntity scrap1 = ScrapEntity.builder().shop(googleShop1).user(user).build();
        ScrapEntity scrap2 = ScrapEntity.builder().shop(googleShop2).user(user).build();
        ScrapEntity scrap3 = ScrapEntity.builder().shop(googleShop3).user(user).build();

        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
        scrapRepository.save(scrap3);

        List<ShopSimpleResponse> dtos = googleShopService.getShops(0, 0, 1, shopRequest);

        Assertions.assertEquals(dtos.size(), 3);
    }

    @Transactional
    @Test
    public void  상점_메인페이지_친구북마크필터() throws Exception {
        //user 회원가입, 로그인
        userRepository.save(user);
        this.tempLoginForTest(user);

        //상점저장
        saveAllShops();

        //팔로우 할 친구들 저장
        userRepository.save(following1);
        userRepository.save(following2);

        //팔로우
        FollowEntity follow1 = FollowEntity.builder()
                .user(user).follower(following1).build();
        FollowEntity follow2 = FollowEntity.builder()
                .user(user).follower(following2).build();

        followRepository.save(follow1);
        followRepository.save(follow2);

        //팔로우한 친구가 리뷰 작성
        ReviewEntity reviewEntity1 = ReviewEntity.builder()
                .shop(googleShop1).writer(following1).content("1").rate(2).build();
        ReviewEntity reviewEntity2 = ReviewEntity.builder()
                .shop(googleShop2).writer(following1).content("2").rate(2).build();
        ReviewEntity reviewEntity3 = ReviewEntity.builder()
                .shop(googleShop2).writer(following2).content("3").rate(2).build();

        reviewRepository.save(reviewEntity1);
        reviewRepository.save(reviewEntity2);
        reviewRepository.save(reviewEntity3);

        //user 북마크
        ScrapEntity scrap1 = ScrapEntity.builder().shop(googleShop3).user(user).build();
        ScrapEntity scrap2 = ScrapEntity.builder().shop(googleShop4).user(user).build();
        ScrapEntity scrap3 = ScrapEntity.builder().shop(googleShop1).user(user).build();

        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
        scrapRepository.save(scrap3);

        List<ShopSimpleResponse> result = this.googleShopService.getShops(0, 1, 1, shopRequest);
        Assertions.assertEquals(result.size(), 4);

    }

    @Transactional
    @Test
    public void 상점_세부정보_북마크여부_반환() throws Exception {
        //user 회원 가입
        userRepository.save(user);

        //임시 로그인
        tempLoginForTest(user);

        //상점 저장
        saveAllShops();

        //북마크 추가
        ScrapEntity scrap1 = ScrapEntity.builder().shop(googleShop1).user(user).build();
        scrapRepository.save(scrap1);

        ShopResponse shopResponse = googleShopService.getShopDetails(googleShop1.getPlaceId(), true);
        Assertions.assertEquals(shopResponse.isScrap(), true);

        ShopResponse shopResponse2 = googleShopService.getShopDetails(googleShop2.getPlaceId(), true);
        Assertions.assertEquals(shopResponse2.isScrap(), false);
    }

    @Test
    public void 자동완성() throws JsonProcessingException {
        AutoCompleteRequest autoCompleteRequest = AutoCompleteRequest.builder()
                .lat(36.3504119)
                .lng(127.3845475).build();

        String query = "순";

        List<String> result = googleShopService.getAutoComplete("순대", autoCompleteRequest);
        Assertions.assertNotEquals(result.size(),0);
    }


    private void saveAllShops() {
        googleShopRepository.save(googleShop1);
        googleShopRepository.save(googleShop2);
        googleShopRepository.save(googleShop3);
        googleShopRepository.save(googleShop4);
    }

    private void tempLoginForTest(UserEntity user) {
        UserDetails userDetails = new CustomUserDetails(user.getId());

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
