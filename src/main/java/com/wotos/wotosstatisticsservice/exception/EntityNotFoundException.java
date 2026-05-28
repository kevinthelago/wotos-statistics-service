package com.wotos.wotosstatisticsservice.exception;

/**
 * Thrown when a requested statistics snapshot or expected-stats row is not present.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
