package org.example.domain.interfaces;

import org.example.domain.Holiday;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public interface NationalHolidays {

    static int currentYear() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        return calendar.get(Calendar.YEAR);
    }

    default List<Holiday> holiday() {
        Holiday holiday = new Holiday();
        holiday.setHolidayType("National");
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear(), Calendar.AUGUST, 15);
        holiday.setDate(calendar.getTime());
        return Collections.singletonList(holiday);
    }

}
