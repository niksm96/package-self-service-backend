package com.pckg.self_service.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitPackageRequest {

    private String packageName;
    private String postalCode;
    private String streetName;
    private String receiverName;
    private String packageSize;
}
