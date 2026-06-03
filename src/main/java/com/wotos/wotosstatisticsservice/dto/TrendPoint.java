package com.wotos.wotosstatisticsservice.dto;

/**
 * A single point on a player's WN8 trend.
 *
 * @param t       the bucket-start instant as an ISO-8601 UTC string (e.g. {@code 2026-03-02T00:00:00Z});
 *                the frontend D3 chart parses this directly
 * @param wn8     the player's overall WN8 at that point
 * @param battles the player's total battles at that point
 */
public record TrendPoint(String t, Float wn8, Integer battles) {
}
