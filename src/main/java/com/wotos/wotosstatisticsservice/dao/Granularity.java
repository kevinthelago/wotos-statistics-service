package com.wotos.wotosstatisticsservice.dao;

/**
 * Time resolution a snapshot is retained at after the retention job collapses the
 * series (see {@code SnapshotRetentionService}): recent data stays {@link #DAILY},
 * mid-age data is thinned to {@link #WEEKLY}, and the oldest data to {@link #MONTHLY}
 * (kept forever).
 */
public enum Granularity {
    DAILY,
    WEEKLY,
    MONTHLY
}
