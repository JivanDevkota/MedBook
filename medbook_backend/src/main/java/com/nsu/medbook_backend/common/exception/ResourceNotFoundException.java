package com.nsu.medbook_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {

    private final HttpStatus status;

    public ResourceNotFoundException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException(resource + " not found with id: " + id);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
