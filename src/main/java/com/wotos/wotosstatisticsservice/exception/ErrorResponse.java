package com.wotos.wotosstatisticsservice.exception;

import java.time.Instant;

/**
 * Standard error body returned by {@link GlobalExceptionHandler} for all
 * exceptions surfaced from controllers.
 */
public record ErrorResponse(int status, String message, Instant timestamp) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, Instant.now());
    }

}
