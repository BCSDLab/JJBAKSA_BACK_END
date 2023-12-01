package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.google.dto.api.inner.openingHours;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayPeriod {
    private Time openTime;
    private Time closeTime;

    public static TodayPeriod createPeriod(openingHours.Period todayPeriod) {
        return new TodayPeriod(Time.from(todayPeriod.getOpen()), Time.from(todayPeriod.getClose()));
    }

    @Getter
    @AllArgsConstructor
    public static class Time {
        int hour;
        int minute;

        public static Time from(openingHours.Period.PeriodTime periodTime) {
            final int SPLIT_INDEX = 2;
            String formattedTime = periodTime.getTime();

            String hour = formattedTime.substring(0, SPLIT_INDEX);
            String minute = formattedTime.substring(SPLIT_INDEX);

            return new Time(Integer.parseInt(hour), Integer.parseInt(minute));
        }
    }
}
