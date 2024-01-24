package com.jjbacsa.jjbacsabackend.util;

import java.util.Random;

public class NameUtil {

    private static String[] prefix = {
            "잘생긴", "멋진", "귀여운", "대담한",
            "섹시한", "이쁜", "당당한", "침착한",
            "명랑한", "용감한", "똑똑한"
    };

    private static String[] suffix= {
            "토마토", "감자", "고구마", "오이",
            "바나나", "사과", "양배추", "레몬",
            "딸기"
    };

    public static String getNewName() {

        Random random = new Random();

        int prefixIdx = random.nextInt(prefix.length);
        int suffixIdx = random.nextInt(suffix.length);

        return prefix[prefixIdx] + suffix[suffixIdx] + random.nextInt(999);
    }

}
