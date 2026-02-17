package org.example.domain;

public class Service {
    private String serviceName;
    private String serviceDescription;
    private double serviceFee;

    // Constructors
    public Service(String serviceName, String serviceDescription, double serviceFee) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.serviceFee = serviceFee;
    }

    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    // toString Method
    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceDescription='" + serviceDescription + '\'' +
                ", serviceFee=" + serviceFee +
                '}';
    }
}
