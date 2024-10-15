package com.pckg.self_service.backend.exception;

public class PackageSelfServiceBackendException extends RuntimeException{
    public PackageSelfServiceBackendException(String message) {
        super(message);
    }

    public PackageSelfServiceBackendException(String message, Throwable cause) {
        super(message, cause);
    }
}
