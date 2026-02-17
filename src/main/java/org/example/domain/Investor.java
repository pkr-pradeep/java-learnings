package org.example.domain;

public class Investor {
    private String investorName;
    private String investorID;
    private double investmentAmount;

    // Constructors
    public Investor(String investorName, String investorID, double investmentAmount) {
        this.investorName = investorName;
        this.investorID = investorID;
        this.investmentAmount = investmentAmount;
    }

    // Getters and Setters
    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getInvestorID() {
        return investorID;
    }

    public void setInvestorID(String investorID) {
        this.investorID = investorID;
    }

    public double getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(double investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    // toString Method
    @Override
    public String toString() {
        return "Investor{" +
                "investorName='" + investorName + '\'' +
                ", investorID='" + investorID + '\'' +
                ", investmentAmount=" + investmentAmount +
                '}';
    }
}
