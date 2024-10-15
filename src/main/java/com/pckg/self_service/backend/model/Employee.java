package com.pckg.self_service.backend.model;

import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Setter
@Getter
@Data
public class Employee {

    private String id;
    private String firstName;
    private String lastName;
    private int age;
    private Address address;

}
