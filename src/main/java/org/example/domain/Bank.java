package org.example.domain;

import java.util.List;

public class Bank {
    private List<Investor> investors;
    private String name;
    private List<Branch> branches;
    private List<Service> services;

    public Bank(List<Investor> investors, String name, List<Branch> branches, List<Service> services) {
        this.investors = investors;
        this.name = name;
        this.branches = branches;
        this.services = services;
    }

    public List<Investor> getInvestors() {
        return investors;
    }

    public void setInvestors(List<Investor> investors) {
        this.investors = investors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "investors=" + investors +
                ", name='" + name + '\'' +
                ", branches=" + branches +
                ", services=" + services +
                '}';
    }
}
