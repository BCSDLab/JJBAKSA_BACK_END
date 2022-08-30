package com.jjbacsa.jjbacsabackend.util;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

import java.util.stream.IntStream;

public class CursorUtil {

    public static String getFollowerCursor(UserEntity user){

        String pad =
                IntStream.range(0, 20 - user.getNickname().length())
                        .mapToObj(i -> "0")
                        .reduce((s, s2) -> s + s2).get();

        return pad + user.getNickname() + String.format("%010d", user.getId());
    }
}
