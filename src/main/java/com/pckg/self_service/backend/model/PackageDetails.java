package com.pckg.self_service.backend.model;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PackageDetails {

    private String packageName;
    private String senderId;
    private String receiverId;
    private ShippingStatus shippingStatus;
    private LocalDate dateOfRegistration;
    private LocalDate dateOfReceipt;
}
