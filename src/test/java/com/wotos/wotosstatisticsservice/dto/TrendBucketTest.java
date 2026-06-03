package com.wotos.wotosstatisticsservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrendBucketTest {

    @Test
    public void defaultsToDayForNullOrBlank() {
        assertEquals(TrendBucket.DAY, TrendBucket.from(null));
        assertEquals(TrendBucket.DAY, TrendBucket.from(""));
        assertEquals(TrendBucket.DAY, TrendBucket.from("   "));
    }

    @Test
    public void parsesDayAndWeekCaseInsensitively() {
        assertEquals(TrendBucket.DAY, TrendBucket.from("day"));
        assertEquals(TrendBucket.WEEK, TrendBucket.from("WEEK"));
        assertEquals(TrendBucket.WEEK, TrendBucket.from("Week"));
    }

    @Test
    public void throwsForUnsupportedBucket() {
        assertThrows(IllegalArgumentException.class, () -> TrendBucket.from("month"));
    }

    @Test
    public void labelIsLowercase() {
        assertEquals("day", TrendBucket.DAY.label());
        assertEquals("week", TrendBucket.WEEK.label());
    }
}
