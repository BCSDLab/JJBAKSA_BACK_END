package com.jjbacsa.jjbacsabackend.scrap.serviceimpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapDirectoryRepository;
import com.jjbacsa.jjbacsabackend.scrap.repository.ScrapRepository;
import com.jjbacsa.jjbacsabackend.scrap.service.InternalScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalScrapServiceImpl implements InternalScrapService {

    private final ScrapRepository scrapRepository;
    private final ScrapDirectoryRepository scrapDirectoryRepository;

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
}
