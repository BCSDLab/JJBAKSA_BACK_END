package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;

import java.util.List;

public interface InternalScrapService {

    ScrapEntity getScrapById(Long scrapId) throws RequestInputException;

    ScrapDirectoryEntity getScrapDirectoryById(Long scrapDirectoryId) throws RequestInputException;

    void addScrapCount(Long scrapDirectoryId, int delta);

    List<Long> getShopIdsForUserScrap() throws Exception;

    boolean isUserScrapShop(Long shopId) throws Exception;
}


