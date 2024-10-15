package com.pckg.self_service.backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShippingOrderDetailsResponse {

    private String packageId;
    private String packageName;
    private String packageSize;
    private String postalCode;
    private String streetName;
    private String receiverName;
    private String orderStatus;
    private String expectedDeliveryDate;
    private String actualDeliveryDateTime;
}
