package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapDirectoryRepository;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapRepository;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import com.jjbacsa.jjbacsabackend.util.CursorUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestConstructor;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
class ScrapServiceTest {

    private final ScrapService scrapService;
    private final InternalUserService userService;
    private final InternalScrapService internalScrapService;

    private final UserRepository userRepository;
    private final GoogleShopRepository googleShopRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapDirectoryRepository scrapDirectoryRepository;

    private final EntityManager entityManager;


    private UserEntity user1;
    private UserEntity user2;
    private GoogleShopEntity googleShop1;
    private GoogleShopEntity googleShop2;
    private GoogleShopEntity googleShop3;
    private GoogleShopEntity googleShop4;
    private GoogleShopEntity googleShop5;
    private GoogleShopEntity googleShop6;
    private GoogleShopEntity googleShop7;
    private GoogleShopEntity googleShop8;
    private GoogleShopEntity googleShop9;
    private GoogleShopEntity googleShop10;
    private GoogleShopEntity googleShop11;
    private List<GoogleShopEntity> googleShops;


    @BeforeEach
    void setup() {

        user1 = userRepository.save(getTestUser("testUser1"));
        user2 = userRepository.save(getTestUser("testUser2"));
        googleShop1 = googleShopRepository.save(getTestShop("ChIJiavaFuyefDUR5wJus9oVECU"));
        googleShop2 = googleShopRepository.save(getTestShop("ChIJe9073fyefDUR4FggnKorNT4"));
        googleShop3 = googleShopRepository.save(getTestShop("ChIJj7qqao2efDURv7SvzRmBV0g"));
        googleShop4 = googleShopRepository.save(getTestShop("ChIJ-0U-4_OefDURVh4e90JaaCo"));
        googleShop5 = googleShopRepository.save(getTestShop("ChIJW3qxgvuefDURVsRM2EnFCz4"));
        googleShop6 = googleShopRepository.save(getTestShop("ChIJER26rO-efDURlGxAEizfpXs"));
        googleShop7 = googleShopRepository.save(getTestShop("ChIJCW9Dz--efDURSbBpRU6lDMA"));
        googleShop8 = googleShopRepository.save(getTestShop("ChIJa_WV4fKefDURc5Gw2KJ8YSE"));
        googleShop9 = googleShopRepository.save(getTestShop("ChIJobJ-VXeffDUR7Rnav5QygYc"));
        googleShop10 = googleShopRepository.save(getTestShop("ChIJkaawQ_KefDURKTDMCMK2Ox4"));

        googleShops = Arrays.asList(googleShop1, googleShop2, googleShop3, googleShop4, googleShop5, googleShop6, googleShop7, googleShop8, googleShop9, googleShop10);
    }

    @DisplayName("스크랩 디렉토리 생성")
    @Test
    void createDirectory() throws Exception {

        //디렉토리 생성
        testLogin(user1);
        ScrapDirectoryResponse dirRes = scrapService.createDirectory(getDirectoryRequest("dir"));
        ScrapDirectoryEntity directory = scrapDirectoryRepository.getById(dirRes.getId());

        //이름이 중복된 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.createDirectory(getDirectoryRequest("dir"))
        );

        //then
        assertEquals(user1, directory.getUser());
    }

    @DisplayName("스크랩 디렉토리 목록 조회")
    @Test
    void getDirectories() throws Exception {

        //테스트 데이터 생성
        for (int i = 0; i < 20; ++i) {
            scrapDirectoryRepository.save(getTestDirectory(user1, "dir" + i));
        }

        //디렉토리 목록 조회
        testLogin(user1);
        Page<ScrapDirectoryResponse> page1 = scrapService.getDirectories(null, 10);
        Page<ScrapDirectoryResponse> page2 = scrapService.getDirectories(CursorUtil.getScrapDirectoryCursor(page1.getContent().get(4)), 15);

        //then
        assertEquals(page2.getContent().get(0).getId(), page1.getContent().get(5).getId());
        assertEquals("dir0", page1.getContent().get(0).getName());
        assertEquals("dir1", page1.getContent().get(1).getName());
        assertEquals("dir10", page1.getContent().get(2).getName());
    }

    @DisplayName("스크랩 디렉토리 수정")
    @Test
    void updateDirectory() throws Exception {

        //테스트 데이터 추가
        ScrapDirectoryEntity dir1 = scrapDirectoryRepository.save(getTestDirectory(user1, "dir1"));
        ScrapDirectoryEntity dir2 = scrapDirectoryRepository.save(getTestDirectory(user2, "dir2"));
        scrapDirectoryRepository.save(getTestDirectory(user1, "dir3"));

        //디렉토리가 존재하지 않을 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.updateDirectory(null, getDirectoryRequest("dir"))
        );

        //내가 만든 디렉토리가 아닐 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.updateDirectory(dir2.getId(), getDirectoryRequest("dir"))
        );

        //이름이 중복된 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.updateDirectory(dir1.getId(), getDirectoryRequest("dir3"))
        );

        //이름 수정
        ScrapDirectoryResponse dirRes = scrapService.updateDirectory(dir1.getId(), getDirectoryRequest("dir"));

        //then
        assertEquals("dir", dir1.getName());
        assertEquals("dir", dirRes.getName());
    }

    @DisplayName("스크랩 디렉토리 제거")
    @Test
    void deleteDirectory() throws Exception {

        //테스트 데이터 생성
        ScrapDirectoryEntity dir1 = scrapDirectoryRepository.save(getTestDirectory(user1, "dir1"));
        ScrapDirectoryEntity dir2 = scrapDirectoryRepository.save(getTestDirectory(user2, "dir2"));
        ScrapEntity scrap = scrapRepository.save(getTestScrap(user1, googleShop1, dir1));
        userService.increaseScrapCount(user1.getId());
        entityManager.flush();
        user1 = userService.getUserById(user1.getId());

        //디렉토리가 존재하지 않을 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.deleteDirectory(null)
        );

        //내가 만든 디렉토리가 아닐 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.deleteDirectory(dir2.getId())
        );

        //디렉토리 제거
        scrapService.deleteDirectory(dir1.getId());
        entityManager.flush();

        //then
        assertEquals(1, dir1.getIsDeleted());
        assertTrue(scrapRepository.findById(scrap.getId()).isEmpty());
        user1 = userService.getUserById(user1.getId());
        assertEquals(0, user1.getUserCount().getScrapCount());
    }

    @DisplayName("스크랩 추가")
    @Test
    void create() throws Exception {

        //테스트 데이터 생성
        ScrapDirectoryEntity dir1 = scrapDirectoryRepository.save(getTestDirectory(user1, "dir1"));
        ScrapDirectoryEntity dir2 = scrapDirectoryRepository.save(getTestDirectory(user2, "dir2"));

        //상점이 없는 경우
        testLogin(user1);
        assertThrows(ApiException.class, () ->
                scrapService.create(getScrapRequest(0L, "없는 거"))
        );

        //디렉토리가 없는 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(null, googleShop1.getPlaceId()))
        );

        //내가 만든 디렉토리가 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(dir2.getId(), googleShop1.getPlaceId()))
        );

        //스크랩 추가
        ScrapResponse scrapRes1 = scrapService.create(getScrapRequest(0L, googleShop1.getPlaceId()));
        ScrapResponse scrapRes2 = scrapService.create(getScrapRequest(dir1.getId(), googleShop2.getPlaceId()));
        ScrapEntity scrap1 = scrapRepository.getById(scrapRes1.getId());
        ScrapEntity scrap2 = scrapRepository.getById(scrapRes2.getId());

        //같은 상점을 중복 추가할 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(0L, googleShop1.getPlaceId()))
        );

        //then
        assertEquals(googleShop1, scrap1.getShop());
        assertEquals(dir1, scrap2.getDirectory());
        assertEquals(1, dir1.getScrapDirectoryCount().getScrapCount());
        assertNull(scrapRes1.getDirectory());
        assertEquals(dir1.getId(), scrapRes2.getDirectory().getId());
        assertEquals(2, user1.getUserCount().getScrapCount());
    }

    @DisplayName("스크랩 목록 조회")
    @Test
    void getScraps() throws Exception {

        //테스트 데이터 생성
        ScrapDirectoryEntity directory1 = scrapDirectoryRepository.save(getTestDirectory(user1, "dir1"));
        ScrapDirectoryEntity directory2 = scrapDirectoryRepository.save(getTestDirectory(user2, "dir2"));

        for (GoogleShopEntity googleShop : googleShops) {
            scrapRepository.save(getTestScrap(user1, googleShop, null));
            scrapRepository.save(getTestScrap(user1, googleShop, directory1));
        }

        //디렉토리가 없는 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.getScraps(null, null, 10)
        );

        //내가 만든 디렉토리가 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.getScraps(directory2.getId(), null, 10)
        );

        //스크랩 조회
        Page<ScrapResponse> page1_1 = scrapService.getScraps(0L, null, 10);
        Page<ScrapResponse> page1_2 = scrapService.getScraps(0L, page1_1.getContent().get(4).getId(), 10);
        Page<ScrapResponse> page2_1 = scrapService.getScraps(directory1.getId(), null, 10);
        Page<ScrapResponse> page2_2 = scrapService.getScraps(directory1.getId(), page2_1.getContent().get(4).getId(), 10);

        //then
        assertEquals(5, page1_2.getContent().size());
        assertEquals(5, page2_2.getContent().size());
        assertEquals(page1_1.getContent().get(5).getId(), page1_2.getContent().get(0).getId());
        assertEquals(page2_1.getContent().get(5).getId(), page2_2.getContent().get(0).getId());

    }

    @DisplayName("스크랩 이동")
    @Test
    void move() throws Exception {

        //테스트 데이터 생성
        ScrapDirectoryEntity directory1 = scrapDirectoryRepository.save(getTestDirectory(user1, "dir1"));
        ScrapDirectoryEntity directory2 = scrapDirectoryRepository.save(getTestDirectory(user2, "dir2"));
        ScrapEntity scrap1 = scrapRepository.save(getTestScrap(user1, googleShop1, null));
        ScrapEntity scrap2 = scrapRepository.save(getTestScrap(user2, googleShop1, null));


        //스크랩이 없는 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.move(0L, getScrapRequest(directory1.getId(), googleShop1.getPlaceId()))
        );

        //내가 만든 스크랩이 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.move(scrap2.getId(), getScrapRequest(directory1.getId(), googleShop1.getPlaceId()))
        );

        //디렉토리가 없는 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.move(scrap1.getId(), getScrapRequest(null, googleShop1.getPlaceId()))
        );

        //내가 만든 디렉토리가 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.move(scrap1.getId(), getScrapRequest(directory2.getId(), googleShop1.getPlaceId()))
        );

        //스크랩 이동
        ScrapResponse res = scrapService.move(scrap1.getId(), getScrapRequest(directory1.getId(), googleShop1.getPlaceId()));

        //then
        assertEquals(directory1.getId(), res.getDirectory().getId());
        assertEquals(directory1, scrap1.getDirectory());
        assertEquals(1, directory1.getScrapDirectoryCount().getScrapCount());
    }

    @DisplayName("스크랩 취소")
    @Test
    void delete() throws Exception {

        //테스트 데이터 생성
        ScrapDirectoryEntity directory = scrapDirectoryRepository.save(getTestDirectory(user1, "dir1"));
        ScrapEntity scrap1 = scrapRepository.save(getTestScrap(user1, googleShop1, directory));
        ScrapEntity scrap2 = scrapRepository.save(getTestScrap(user2, googleShop1, null));
        userService.increaseScrapCount(user1.getId());
        internalScrapService.addScrapCount(directory.getId(), 1);

        //스크랩이 없는 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.delete(0L)
        );

        //내가 만든 스크랩이 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.delete(scrap2.getId())
        );

        //스크랩 제거
        scrapService.delete(scrap1.getId());

        //then
        assertEquals(1, scrap1.getIsDeleted());
        assertEquals(0, user1.getUserCount().getScrapCount());
        assertEquals(0, directory.getScrapDirectoryCount().getScrapCount());
    }

    @DisplayName("사용자가 스크랩한 상점 반환")
    @Test
    void getUserScrap() throws Exception {
        //사용자 로그인
        testLogin(user1);

        scrapRepository.save(getTestScrap(user1, googleShop1, null));
        scrapRepository.save(getTestScrap(user1, googleShop2, null));
        scrapRepository.save(getTestScrap(user1, googleShop3, null));
        scrapRepository.save(getTestScrap(user1, googleShop4, null));
        scrapRepository.save(getTestScrap(user1, googleShop5, null));
        scrapRepository.save(getTestScrap(user1, googleShop6, null));
        scrapRepository.save(getTestScrap(user1, googleShop7, null));
        scrapRepository.save(getTestScrap(user1, googleShop8, null));
        scrapRepository.save(getTestScrap(user1, googleShop9, null));
        scrapRepository.save(getTestScrap(user1, googleShop10, null)); //떡군이네떡볶이영등포점


        Page<ShopScrapResponse> result = scrapService.getScrapShops(null, null, 5);
        Assertions.assertEquals(5, result.getContent().size());

        for (ShopScrapResponse s : result.getContent()) {
            Assertions.assertNotNull(s.getScrapId());
            Assertions.assertNotEquals(s.getName(), "남천할매떡볶이 신세계백화점 영등포점");
        }

        Page<ShopScrapResponse> result2 = scrapService.getScrapShops(null, result.getContent().get(4).getScrapId(), 10);
        Assertions.assertEquals(5, result2.getContent().size());
        Assertions.assertEquals("떡군이네떡볶이영등포점", result2.getContent().get(4).getName());
    }

    @DisplayName("개별 스크랩 상점 정보")
    @Test
    void getScrapShop() throws Exception {
        testLogin(user1);

        ScrapEntity scrap = scrapRepository.save(getTestScrap(user1, googleShop1, null));
        ShopScrapResponse shopScrapResponse = scrapService.getScrapShop(scrap.getId());

        Assertions.assertNotNull(shopScrapResponse.getAddress());
    }

    private UserEntity getTestUser(String account) {

        return UserEntity.builder()
                .account(account)
                .password("password")
                .email("test2@google.com")
                .nickname(account)
                .userType(UserType.NORMAL)
                .build();
    }

    private GoogleShopEntity getTestShop(String placeId) {

        return GoogleShopEntity.builder()
                .placeId(placeId).build();
    }

    private ScrapDirectoryEntity getTestDirectory(UserEntity user, String name) {

        return ScrapDirectoryEntity.builder()
                .user(user)
                .name(name)
                .build();
    }

    private ScrapEntity getTestScrap(UserEntity user, GoogleShopEntity shop, ScrapDirectoryEntity directory) {

        return ScrapEntity.builder()
                .directory(directory)
                .user(user)
                .shop(shop)
                .build();
    }

    private ScrapDirectoryRequest getDirectoryRequest(String name) {

        return ScrapDirectoryRequest.builder()
                .name(name)
                .build();
    }

    private ScrapRequest getScrapRequest(Long directoryId, String placeId) {

        return ScrapRequest.builder()
                .directoryId(directoryId)
                .placeId(placeId)
                .build();
    }

    private void testLogin(UserEntity user) throws Exception {

        UserDetails userDetails = new CustomUserDetails(user.getId());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        //Mockito.when(userService.getLoginUser()).thenReturn(user);
    }
}