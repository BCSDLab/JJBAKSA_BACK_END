package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.mapper.FollowMapper;
import com.jjbacsa.jjbacsabackend.follow.mapper.FollowRequestMapper;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRequestRepository;
import com.jjbacsa.jjbacsabackend.follow.service.FollowService;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.mapper.ScrapDirectoryMapper;
import com.jjbacsa.jjbacsabackend.scrap.mapper.ScrapMapper;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapDirectoryRepository;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapRepository;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapServiceImpl implements ScrapService {

    private final UserService userService;

    private final UserRepository userRepository;
    private final ScrapDirectoryRepository scrapDirectoryRepository;
    private final ScrapRepository scrapRepository;
    private final ShopRepository shopRepository;


    @Override
    public ScrapDirectoryResponse createDirectory(ScrapDirectoryRequest request) throws Exception {

        UserEntity user = getLoginUser();

        checkDirectoryDuplication(user, request);

        ScrapDirectoryEntity directory = saveDirectory(user, request);

        return ScrapDirectoryMapper.INSTANCE.toScrapDirectoryResponse(directory);
    }

    @Override
    public Page<ScrapDirectoryResponse> getDirectories(String cursor, Integer pageSize) throws Exception {

        UserEntity user = getLoginUser();
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ScrapDirectoryEntity> directories = scrapDirectoryRepository.findAllByUserWithCursor(user, cursor, pageable);

        return directories.map(ScrapDirectoryMapper.INSTANCE::toScrapDirectoryResponse);
    }

    @Override
    public ScrapDirectoryResponse updateDirectory(Long directoryId, ScrapDirectoryRequest request) throws Exception {

        UserEntity user = getLoginUser();
        ScrapDirectoryEntity directory = getDirectory(directoryId);

        checkDirectoryOwner(user, directory);
        checkDirectoryDuplication(user, request);

        directory.update(request);

        return ScrapDirectoryMapper.INSTANCE.toScrapDirectoryResponse(directory);
    }

    @Override
    public void deleteDirectory(Long directoryId) throws Exception {

        UserEntity user = getLoginUser();
        ScrapDirectoryEntity directory = getDirectory(directoryId);

        checkDirectoryOwner(user, directory);

        deleteDirectory(directory);
    }

    @Override
    public ScrapResponse create(ScrapRequest request) throws Exception {

        UserEntity user = getLoginUser();
        ShopEntity shop = getShop(request.getShopId());
        ScrapDirectoryEntity directory = getDirectoryOrNull(request.getDirectoryId());

        if (directory != null)
            checkDirectoryOwner(user, directory);
        checkScrapDuplication(user, shop);

        ScrapEntity scrap = saveScrap(user, shop, directory);

        return ScrapMapper.INSTANCE.toScrapResponse(scrap);
    }

    @Override
    public Page<ScrapResponse> getScraps(Long directoryId, Long cursor, Integer pageSize) throws Exception {

        UserEntity user = getLoginUser();
        ScrapDirectoryEntity directory = getDirectoryOrNull(directoryId);

        if (directory != null)
            checkDirectoryOwner(user, directory);

        Page<ScrapEntity> scraps = scrapRepository.findAllByUserAndDirectoryWithCursor(user, directory, cursor, PageRequest.of(0, pageSize));

        return scraps.map(ScrapMapper.INSTANCE::toScrapResponse);
    }

    private void moveScrap(ScrapEntity scrap, ScrapDirectoryEntity directory) {

        if (Objects.equals(directory, scrap.getDirectory()))
            return;

        if (scrap.getDirectory() != null)
            scrap.getDirectory().getScrapDirectoryCount().decreaseScrapCount();

        if (directory != null)
            directory.getScrapDirectoryCount().increaseScrapCount();

        scrap.setDirectory(directory);
    }

    @Override
    public ScrapResponse move(Long scrapId, ScrapRequest request) throws Exception {

        UserEntity user = getLoginUser();
        ScrapEntity scrap = getScrap(scrapId);
        ScrapDirectoryEntity directory = getDirectoryOrNull(request.getDirectoryId());

        if (directory != null)
            checkDirectoryOwner(user, directory);
        checkScrapOwner(user, scrap);

        moveScrap(scrap, directory);

        return ScrapMapper.INSTANCE.toScrapResponse(scrap);
    }

    @Override
    public void delete(Long scrapId) throws Exception {

        UserEntity user = getLoginUser();
        ScrapEntity scrap = getScrap(scrapId);

        checkScrapOwner(user, scrap);

        deleteScrap(scrap);
    }

    private UserEntity getLoginUser() throws Exception {

        return userRepository.findById(userService.getLoginUser().getId())
                .orElseThrow(() -> new RuntimeException("User not logged in."));
    }

    private ScrapDirectoryEntity getDirectory(Long directoryId) {

        return scrapDirectoryRepository.findById(directoryId)
                .orElseThrow(() -> new RuntimeException("Directory Not Exists."));
    }

    private ScrapDirectoryEntity getDirectoryOrNull(Long directoryId) {

        if (directoryId == null)
            return null;

        return getDirectory(directoryId);
    }

    private ShopEntity getShop(Long shopId) {

        return shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop Not Exists."));
    }

    private ScrapEntity getScrap(Long scrapId) {

        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new RuntimeException("Scrap Not Exists."));
    }

    private void checkDirectoryDuplication(UserEntity user, ScrapDirectoryRequest request) {

        if (scrapDirectoryRepository.existsByUserAndName(user, request.getName()))
            throw new RuntimeException("Directory already exists.");
    }

    private void checkDirectoryOwner(UserEntity user, ScrapDirectoryEntity directory) {

        if (!user.equals(directory.getUser()))
            throw new RuntimeException("Directory Not Exists.");
    }

    private void checkScrapDuplication(UserEntity user, ShopEntity shop) {

        if (scrapRepository.existsByUserAndShop(user, shop))
            throw new RuntimeException("Scrap already exists.");
    }

    private void checkScrapOwner(UserEntity user, ScrapEntity scrap) {

        if (!user.equals(scrap.getUser()))
            throw new RuntimeException("Scrap Not Exists.");
    }

    private ScrapDirectoryEntity saveDirectory(UserEntity user, ScrapDirectoryRequest request) {

        ScrapDirectoryEntity directory = ScrapDirectoryEntity.builder()
                .user(user)
                .name(request.getName())
                .build();

        return scrapDirectoryRepository.save(directory);
    }

    private ScrapEntity saveScrap(UserEntity user, ShopEntity shop, ScrapDirectoryEntity directory) {

        ScrapEntity scrap = ScrapEntity.builder()
                .user(user)
                .shop(shop)
                .directory(directory)
                .build();

        user.getUserCount().increaseScrapCount();
        if (directory != null)
            directory.getScrapDirectoryCount().increaseScrapCount();

        return scrapRepository.save(scrap);
    }

    private void deleteDirectory(ScrapDirectoryEntity directory) {

        int scrapCount = (int) scrapRepository.deleteAllByDirectory(directory);
        directory.getUser().getUserCount().addScrapCount(-scrapCount);
        directory.getScrapDirectoryCount().addScrapCount(-scrapCount);
        directory.setIsDeleted(1);
    }

    private void deleteScrap(ScrapEntity scrap) {

        scrap.getUser().getUserCount().decreaseScrapCount();

        if (scrap.getDirectory() != null)
            scrap.getDirectory().getScrapDirectoryCount().decreaseScrapCount();

        scrap.setIsDeleted(1);
    }
}
