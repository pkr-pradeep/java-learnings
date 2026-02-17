package org.example.masking;

import org.example.domain.interfaces.PII;
import org.example.utilities.PiiSafeToString;

public class MaskedPojo extends PiiSafeToString {
    private String name;
    @PII(mask = "****")
    private String email;
    @PII(mask = "####")
    private Integer phoneNumber;
    private String address;
    private String id;

    public MaskedPojo(String name, String email, Integer phoneNumber, String address, String id) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public Integer getPhoneNumber() {
        return phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public String getId() {
        return id;
    }
}
