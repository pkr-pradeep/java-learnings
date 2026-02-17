package org.example.masking;

public class MaskingMain {
    public static void main(String[] args) {
        MaskedPojo pojo = new MaskedPojo("John Doe", "john.doe@email.com", 1234567890, "123 Main St", "ID12345");
        System.out.println(pojo);
        MaskedPojo2 maskedPojo2 = new MaskedPojo2("John Doe", "john.doe@email.com", 1234567890, "123 Main St", "ID12345");
        System.out.println(maskedPojo2);
    }
}
