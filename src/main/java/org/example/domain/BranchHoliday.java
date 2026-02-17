package org.example.domain;

import org.example.domain.interfaces.NationalHolidays;

import java.util.Date;

public class BranchHoliday implements NationalHolidays {
    private Date date;
    private String holidayType; // National or Regional
    private String occasionName;

    // Constructors
    public BranchHoliday(Date date, String holidayType, String occasionName) {
        this.date = date;
        this.holidayType = holidayType;
        this.occasionName = occasionName;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(String holidayType) {
        this.holidayType = holidayType;
    }

    public String getOccasionName() {
        return occasionName;
    }

    public void setOccasionName(String occasionName) {
        this.occasionName = occasionName;
    }

    // toString Method
    @Override
    public String toString() {
        return "BranchHoliday{" +
                "date=" + date +
                ", holidayType='" + holidayType + '\'' +
                ", occasionName='" + occasionName + '\'' +
                '}';
    }
}
