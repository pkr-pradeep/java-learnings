package org.example.domain;

import org.example.domain.interfaces.NationalHolidays;

import java.util.ArrayList;
import java.util.List;

public class Branch implements NationalHolidays {
    private final String branchName;
    private final String cityName;
    private final int postalCode;
    private final String stateName;
    private final List<Employee> employees;
    private final List<Customer> customers;
    private final List<Holiday> holidays;

    public Branch(String branchName, String cityName, int postalCode, String stateName) {
        this.branchName = branchName;
        this.cityName = cityName;
        this.postalCode = postalCode;
        this.stateName = stateName;
        this.employees = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.holidays = new ArrayList<>(holiday()); // Add national holidays by default
    }

    // Additional methods to add regional holidays
    public void addRegionalHoliday(Holiday holiday) {
        holidays.add(holiday);
    }

    // Getters, Setters, and toString methods

    @Override
    public String toString() {
        return "Branch{" +
                "branchName='" + branchName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", postalCode=" + postalCode +
                ", stateName='" + stateName + '\'' +
                ", employees=" + employees +
                ", customers=" + customers +
                ", holidays=" + holidays +
                '}';
    }
}
