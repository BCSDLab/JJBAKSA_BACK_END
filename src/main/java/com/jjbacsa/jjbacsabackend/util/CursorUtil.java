package com.jjbacsa.jjbacsabackend.util;

import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.mapper.ScrapDirectoryMapper;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

import java.util.stream.IntStream;

public class CursorUtil {

    public static String getFollowerCursor(UserEntity user) {

        String pad = IntStream.range(0, 20 - user.getNickname().length())
                .mapToObj(i -> "0")
                .reduce((s, s2) -> s + s2).get();

        return pad + user.getNickname() + String.format("%010d", user.getId());
    }

    public static String getScrapDirectoryCursor(ScrapDirectoryResponse directory) {

        String pad = IntStream.range(0, 10 - directory.getName().length())
                .mapToObj(i -> "0")
                .reduce((s, s2) -> s + s2).get();

        return pad + directory.getName() + String.format("%010d", directory.getId());
    }

    public static String getScrapDirectoryCursor(ScrapDirectoryEntity directory) {

        return getScrapDirectoryCursor(ScrapDirectoryMapper.INSTANCE.toScrapDirectoryResponse(directory));
    }
}
