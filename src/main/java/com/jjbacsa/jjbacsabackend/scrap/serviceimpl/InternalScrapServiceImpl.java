package com.jjbacsa.jjbacsabackend.scrap.serviceimpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapDirectoryRepository;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapRepository;
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalScrapServiceImpl implements InternalScrapService {

    private final ScrapRepository scrapRepository;
    private final ScrapDirectoryRepository scrapDirectoryRepository;
    private final InternalUserService internalUserService;
//    private final InternalGoogleService internalGoogleService;

    @Override
    public ScrapEntity getScrapById(Long scrapId) throws RequestInputException {

        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SCRAP_NOT_EXISTS_EXCEPTION));
    }

    @Override
    public ScrapDirectoryEntity getScrapDirectoryById(Long scrapDirectoryId) throws RequestInputException {

        return scrapDirectoryRepository.findById(scrapDirectoryId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SCRAP_DIRECTORY_NOT_EXISTS_EXCEPTION));
    }

    @Override
    @Transactional
    public void addScrapCount(Long scrapDirectoryId, int delta) {

        ScrapDirectoryEntity scrapDirectory = getScrapDirectoryById(scrapDirectoryId);
        scrapDirectory.getScrapDirectoryCount().setScrapCount(scrapDirectoryRepository.getScrapCount(scrapDirectoryId) + delta);
    }

    //현재 사용자가 스크랩 한 상점 id 가져오기
    @Override
    public List<Long> getShopIdsForUserScrap() throws Exception {
        UserEntity user=internalUserService.getLoginUser();

        return scrapRepository.findAllByUser(user)
                .stream()
                .map(ScrapEntity::getShop)
                .map(GoogleShopEntity::getId)
                .collect(Collectors.toList());
    }

    //상점 id와 사용자 id 비교해서 현재 사용자가 북마크 하는지 여부 반환
    @Override
    public boolean isUserScrapShop(GoogleShopEntity googleShop) throws Exception {

        UserEntity user=internalUserService.getLoginUser();

        return scrapRepository.existsByUserAndShop(user, googleShop);
    }
}
