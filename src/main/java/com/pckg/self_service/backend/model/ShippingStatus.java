package com.pckg.self_service.backend.model;

import java.util.stream.Stream;

public enum ShippingStatus {
    IN_PROGRESS,
    SENT,
    DELIVERED;

    /**
     * Validate whether the provided status is a valid status.
     * @param inputStatus status
     * @return true/false: if a status is valid or not.
     */
    public static boolean validateStatus(String inputStatus) {
        return Stream.of(values())
                .anyMatch(status -> status.name().equalsIgnoreCase(inputStatus));
    }
}
