package com.wotos.wotosstatisticsservice.dto;

import java.util.List;

/**
 * Response body for {@code GET /api/stats/players/{accountId}/trend}.
 *
 * <p>This shape is a cross-stream contract: the edge service fans the request out
 * to this endpoint unchanged, and the frontend WN8 view consumes {@code points}
 * (with {@code t} as ISO-8601 UTC). Coordinate any change via the fleet.
 *
 * @param accountId the WoT account the trend belongs to
 * @param bucket    the bucketing granularity used ({@code "day"} or {@code "week"})
 * @param points    the trend points, ordered oldest-first
 */
public record PlayerTrendResponse(Integer accountId, String bucket, List<TrendPoint> points) {
}
