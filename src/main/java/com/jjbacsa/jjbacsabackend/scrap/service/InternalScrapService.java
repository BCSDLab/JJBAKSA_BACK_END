package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;

public interface InternalScrapService {

    ScrapEntity getScrapById(Long scrapId) throws RequestInputException;

    ScrapDirectoryEntity getScrapDirectoryById(Long scrapDirectoryId) throws RequestInputException;

    void addScrapCount(Long scrapDirectoryId, int delta);
}
