package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
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
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.CursorUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestConstructor;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
class ScrapServiceTest {

    private final ScrapService scrapService;
    @MockBean
    private final UserService userService;

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapDirectoryRepository scrapDirectoryRepository;


    private UserEntity user1;
    private UserEntity user2;
    private ShopEntity shop1;
    private ShopEntity shop2;

    @BeforeEach
    void setup() {

        user1 = userRepository.save(getTestUser("testUser1"));
        user2 = userRepository.save(getTestUser("testUser2"));
        shop1 = shopRepository.save(getTestShop("testShop1"));
        shop2 = shopRepository.save(getTestShop("testShop2"));
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
                scrapService.updateDirectory(0L, getDirectoryRequest("dir"))
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
        ScrapEntity scrap = scrapRepository.save(getTestScrap(user1, shop1, dir1));
        user1.getUserCount().increaseScrapCount();

        //디렉토리가 존재하지 않을 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.deleteDirectory(0L)
        );

        //내가 만든 디렉토리가 아닐 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.deleteDirectory(dir2.getId())
        );

        //디렉토리 제거
        scrapService.deleteDirectory(dir1.getId());

        //then
        assertEquals(1, dir1.getIsDeleted());
        assertTrue(scrapRepository.findById(scrap.getId()).isEmpty());
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
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(null, 0L))
        );

        //디렉토리가 없는 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(0L, shop1.getId()))
        );

        //내가 만든 디렉토리가 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(dir2.getId(), shop1.getId()))
        );

        //스크랩 추가
        ScrapResponse scrapRes1 = scrapService.create(getScrapRequest(null, shop1.getId()));
        ScrapResponse scrapRes2 = scrapService.create(getScrapRequest(dir1.getId(), shop2.getId()));
        ScrapEntity scrap1 = scrapRepository.getById(scrapRes1.getId());
        ScrapEntity scrap2 = scrapRepository.getById(scrapRes2.getId());

        //같은 상점을 중복 추가할 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.create(getScrapRequest(null, shop1.getId()))
        );

        //then
        assertEquals(shop1, scrap1.getShop());
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
        for (int i = 0; i < 10; ++i) {

            ShopEntity shop1 = shopRepository.save(getTestShop("shop0" + i));
            ShopEntity shop2 = shopRepository.save(getTestShop("shop1" + i));
            scrapRepository.save(getTestScrap(user1, shop1, null));
            scrapRepository.save(getTestScrap(user1, shop2, directory1));
        }

        //디렉토리가 없는 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.getScraps(0L, null, 10)
        );

        //내가 만든 디렉토리가 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.getScraps(directory2.getId(), null, 10)
        );

        //스크랩 조회
        Page<ScrapResponse> page1_1 = scrapService.getScraps(null, null, 10);
        Page<ScrapResponse> page1_2 = scrapService.getScraps(null, page1_1.getContent().get(4).getId(), 10);
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
        ScrapEntity scrap1 = scrapRepository.save(getTestScrap(user1, shop1, null));
        ScrapEntity scrap2 = scrapRepository.save(getTestScrap(user2, shop1, null));


        //스크랩이 없는 경우
        testLogin(user1);
        assertThrows(RuntimeException.class, () ->
                scrapService.move(0L, getScrapRequest(directory1.getId(), 0L))
        );

        //내가 만든 스크랩이 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.move(scrap2.getId(), getScrapRequest(directory1.getId(), 0L))
        );

        //디렉토리가 없는 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.move(scrap1.getId(), getScrapRequest(0L, 0L))
        );

        //내가 만든 디렉토리가 아닌 경우
        assertThrows(RuntimeException.class, () ->
                scrapService.move(scrap1.getId(), getScrapRequest(directory2.getId(), 0L))
        );

        //스크랩 이동
        ScrapResponse res = scrapService.move(scrap1.getId(), getScrapRequest(directory1.getId(), 0L));

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
        ScrapEntity scrap1 = scrapRepository.save(getTestScrap(user1, shop1, directory));
        ScrapEntity scrap2 = scrapRepository.save(getTestScrap(user2, shop1, null));
        user1.getUserCount().increaseScrapCount();
        directory.getScrapDirectoryCount().increaseScrapCount();

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

    private UserEntity getTestUser(String account) {

        return UserEntity.builder()
                .account(account)
                .password("password")
                .email("test2@google.com")
                .nickname(account)
                .userType(UserType.NORMAL)
                .build();
    }

    private ShopEntity getTestShop(String name) {

        return ShopEntity.builder()
                .placeId(name)
                .placeName(name)
                .x("0")
                .y("0")
                .categoryName("category")
                .build();
    }

    private ScrapDirectoryEntity getTestDirectory(UserEntity user, String name) {

        return ScrapDirectoryEntity.builder()
                .user(user)
                .name(name)
                .build();
    }

    private ScrapEntity getTestScrap(UserEntity user, ShopEntity shop, ScrapDirectoryEntity directory) {

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

    private ScrapRequest getScrapRequest(Long directoryId, Long shopId) {

        return ScrapRequest.builder()
                .directoryId(directoryId)
                .shopId(shopId)
                .build();
    }

    private void testLogin(UserEntity user) throws Exception {

        Mockito.when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user));
    }
}