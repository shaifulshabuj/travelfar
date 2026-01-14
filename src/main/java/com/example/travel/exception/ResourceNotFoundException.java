package com.example.travel.exception;

/**
 * Custom exception for resource not found scenarios.
 * Used when hotel or reservation is not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
