package com.example.url_shortner.exception;

/**
 * Custom exception for when a requested URL short code is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

