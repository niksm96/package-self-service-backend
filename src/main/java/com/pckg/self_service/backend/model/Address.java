package com.pckg.self_service.backend.model;

import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Setter
@Getter
@Data
public class Address {

    private String id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
