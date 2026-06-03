package com.wotos.wotosstatisticsservice.dto;

/**
 * Time-bucketing granularity for the player WN8 trend endpoint.
 */
public enum TrendBucket {
    DAY,
    WEEK;

    /**
     * Parses the {@code bucket} query parameter.
     *
     * @param value the raw query value, case-insensitive; {@code null}/blank defaults to {@link #DAY}
     * @return the matching bucket
     * @throws IllegalArgumentException if the value is neither {@code day} nor {@code week}
     */
    public static TrendBucket from(String value) {
        if (value == null || value.isBlank()) {
            return DAY;
        }
        return switch (value.toLowerCase()) {
            case "day" -> DAY;
            case "week" -> WEEK;
            default -> throw new IllegalArgumentException(
                    "Invalid bucket '" + value + "'; expected 'day' or 'week'");
        };
    }

    /** The lowercase label echoed back in the response (e.g. {@code "day"}). */
    public String label() {
        return name().toLowerCase();
    }
}
