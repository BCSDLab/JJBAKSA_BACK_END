package com.jjbacsa.jjbacsabackend.etc.enums;

import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;

public enum WeekType {
    SUN, MON, TUE, WED, THR, FRI, SAT;

    public static WeekType getWeekType(int week) {
        validateWeek(week);
        return WeekType.values()[week];
    }

    public static WeekType getWeekTypeByCalender(int dayOfWeekNumber) {
        return getWeekType(dayOfWeekNumber - 1);
    }

    public int getWeekNumber() {
        return this.ordinal();
    }

    private static void validateWeek(int week) {
        if (!(week >= 0 && week <= 6)) {
            throw new ApiException(ErrorMessage.WEEK_DAY_EXCEPTION);
        }
    }
}
