package org.example.masking;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MaskedPojo2 {
    private String name;
    @ToString.Exclude
    private String email;
    @ToString.Exclude
    private Integer phoneNumber;
    private String address;
    private String id;
}
