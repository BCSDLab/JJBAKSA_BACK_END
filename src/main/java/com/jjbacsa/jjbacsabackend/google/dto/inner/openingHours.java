package com.jjbacsa.jjbacsabackend.google.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class openingHours {

    @JsonProperty("open_now")
    String openNow; //현재 open 여부

    @JsonProperty("weekday_text")
    List<String> weekdayText; //영업시간 정보 (쿼리 다중 검색에서는 제공하지 않음)
}
