package org.example.pojomanipulation;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonMerge;

import static org.example.pojomanipulation.OrgExample.nvl;

@JsonIgnoreProperties(ignoreUnknown = true)
    public class Organization {
        private String id;
        private String name;
        private String headquarter; // departmentId or addressId depending on how you model HQ
        private List<Employee> employees;
        private List<Department> departments;
        private List<Service> services;
        @JsonMerge
        private Address addresses;
        private List<Holiday> holidays;
        private BigDecimal revenue;

        // getters/setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHeadquarter() { return headquarter; }
        public void setHeadquarter(String headquarter) { this.headquarter = headquarter; }
        public List<Employee> getEmployees() { return employees; }
        public void setEmployees(List<Employee> employees) { this.employees = nvl(employees); }
        public List<Department> getDepartments() { return departments; }
        public void setDepartments(List<Department> departments) { this.departments = nvl(departments); }
        public List<Service> getServices() { return services; }
        public void setServices(List<Service> services) { this.services = nvl(services); }
        public Address getAddresses() { return addresses; }
        public void setAddresses(Address addresses) { this.addresses = (addresses); }
        public List<Holiday> getHolidays() { return holidays; }
        public void setHolidays(List<Holiday> holidays) { this.holidays = nvl(holidays); }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue != null ? revenue : BigDecimal.ZERO; }
    }