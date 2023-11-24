package com.jjbacsa.jjbacsabackend.google.dto.response;


import com.jjbacsa.jjbacsabackend.etc.enums.WeekType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class Periods {
    Period[] periods;

    private Periods(Period[] periods) {
        this.periods = periods;
    }

    public static Periods createPeriods(List<Period> apiPeriods) {
        Period[] periods = new Period[7];

        for (Period period : apiPeriods) {
            WeekType week = period.getWeek();

            periods[week.getWeekNumber()] = period;
        }

        return new Periods(periods);
    }

    @AllArgsConstructor
    @Getter
    public static class Period {
        private WeekType week;
        private int openTime;
        private int closeTime;
    }
}
