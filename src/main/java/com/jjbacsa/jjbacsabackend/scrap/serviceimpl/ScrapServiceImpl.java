package com.jjbacsa.jjbacsabackend.scrap.serviceimpl;

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
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import com.jjbacsa.jjbacsabackend.scrap.service.ScrapService;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.service.InternalShopService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
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

    private final InternalUserService userService;
    private final InternalShopService shopService;
    private final InternalScrapService scrapService;

    private final ScrapDirectoryRepository scrapDirectoryRepository;
    private final ScrapRepository scrapRepository;


    @Override
    public ScrapDirectoryResponse createDirectory(ScrapDirectoryRequest request) throws Exception {

        UserEntity user = userService.getLoginUser();

        checkDirectoryDuplication(user, request);

        ScrapDirectoryEntity directory = saveDirectory(user, request);

        return ScrapDirectoryMapper.INSTANCE.toScrapDirectoryResponse(directory);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ScrapDirectoryResponse> getDirectories(String cursor, Integer pageSize) throws Exception {

        UserEntity user = userService.getLoginUser();
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ScrapDirectoryEntity> directories = scrapDirectoryRepository.findAllByUserWithCursor(user, cursor, pageable);

        return directories.map(ScrapDirectoryMapper.INSTANCE::toScrapDirectoryResponse);
    }

    @Override
    public ScrapDirectoryResponse updateDirectory(Long directoryId, ScrapDirectoryRequest request) throws Exception {

        UserEntity user = userService.getLoginUser();
        ScrapDirectoryEntity directory = scrapService.getScrapDirectoryById(directoryId);

        checkDirectoryOwner(user, directory);
        checkDirectoryDuplication(user, request);

        directory.update(request);

        return ScrapDirectoryMapper.INSTANCE.toScrapDirectoryResponse(directory);
    }

    @Override
    public void deleteDirectory(Long directoryId) throws Exception {

        UserEntity user = userService.getLoginUser();
        ScrapDirectoryEntity directory = scrapService.getScrapDirectoryById(directoryId);

        checkDirectoryOwner(user, directory);

        deleteDirectory(directory);
    }

    @Override
    public ScrapResponse create(ScrapRequest request) throws Exception {

        UserEntity user = userService.getLoginUser();
        ShopEntity shop = shopService.getShopById(request.getShopId());
        ScrapDirectoryEntity directory = getDirectoryOrNull(request.getDirectoryId());

        if (directory != null)
            checkDirectoryOwner(user, directory);
        checkScrapDuplication(user, shop);

        ScrapEntity scrap = saveScrap(user, shop, directory);

        return ScrapMapper.INSTANCE.toScrapResponse(scrap);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ScrapResponse> getScraps(Long directoryId, Long cursor, Integer pageSize) throws Exception {

        UserEntity user = userService.getLoginUser();
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
            scrapService.addScrapCount(scrap.getDirectory().getId(), -1);

        if (directory != null)
            scrapService.addScrapCount(directory.getId(), 1);

        scrap.setDirectory(directory);
    }

    @Override
    public ScrapResponse move(Long scrapId, ScrapRequest request) throws Exception {

        UserEntity user = userService.getLoginUser();
        ScrapEntity scrap = scrapService.getScrapById(scrapId);
        ScrapDirectoryEntity directory = getDirectoryOrNull(request.getDirectoryId());

        if (directory != null)
            checkDirectoryOwner(user, directory);
        checkScrapOwner(user, scrap);

        moveScrap(scrap, directory);

        return ScrapMapper.INSTANCE.toScrapResponse(scrap);
    }

    @Override
    public void delete(Long scrapId) throws Exception {

        UserEntity user = userService.getLoginUser();
        ScrapEntity scrap = scrapService.getScrapById(scrapId);

        checkScrapOwner(user, scrap);

        deleteScrap(scrap);
    }

    private ScrapDirectoryEntity getDirectoryOrNull(Long directoryId) {

        if (directoryId == 0L)
            return null;

        return scrapService.getScrapDirectoryById(directoryId);
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

        userService.increaseScrapCount(user.getId());
        if (directory != null)
            scrapService.addScrapCount(directory.getId(), 1);

        return scrapRepository.save(scrap);
    }

    private void deleteDirectory(ScrapDirectoryEntity directory) {

        int scrapCount = (int) scrapRepository.deleteAllByDirectory(directory);
        userService.addScrapCount(directory.getUser().getId(), -scrapCount);
        scrapService.addScrapCount(directory.getId(), -scrapCount);
        directory.setIsDeleted(1);
    }

    private void deleteScrap(ScrapEntity scrap) {

        userService.decreaseScrapCount(scrap.getUser().getId());

        if (scrap.getDirectory() != null)
            scrapService.addScrapCount(scrap.getDirectory().getId(), -1);

        scrap.setIsDeleted(1);
    }
}
