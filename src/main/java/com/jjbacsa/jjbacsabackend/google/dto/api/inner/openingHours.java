package com.jjbacsa.jjbacsabackend.google.dto.api.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpeningHours {

    @JsonProperty("open_now")
    String openNow; //현재 open 여부

    @JsonProperty("periods")
    List<Period> periods; //영업시간 정보 객체화

    @Data
    public static class Period{
        PeriodTime open;
        PeriodTime close;

        @Data
        public static class PeriodTime{
            Integer day; //일요일(0)~토요일(6)
            String time; //0000-2359
        }
    }
}