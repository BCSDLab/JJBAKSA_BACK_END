package com.jjbacsa.jjbacsabackend.google.dto.inner;

import lombok.Data;

import java.util.List;

@Data
public class Opening_hours {
    String open_now; //현재 open 여부
    List<String> weekday_text; //영업시간 정보 (쿼리 다중 검색에서는 제공하지 않음)
}
