package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.dao.Granularity;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.SnapshotRetentionService.RetentionPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure (no Spring context / database) tests for the retention collapse logic.
 */
public class SnapshotRetentionServiceTest {

    private static final long SECONDS_PER_DAY = 86_400L;
    /** A whole-day-aligned reference "now" (epoch day 19000) so day buckets are exact. */
    private static final long NOW_DAY = 19_000L;

    private SnapshotRetentionService service;

    @BeforeEach
    public void setUp() {
        service = new SnapshotRetentionService(null);
        // Compressed windows so a 90-day series exercises all three zones:
        // daily for the last 30 days, weekly up to day 86, monthly beyond.
        ReflectionTestUtils.setField(service, "dailyWindowDays", 30);
        ReflectionTestUtils.setField(service, "weeklyWindowDays", 86);
    }

    @Test
    public void ninetyDayDailySeriesCollapsesTo30Daily8Weekly1Monthly() {
        Instant now = Instant.ofEpochSecond(NOW_DAY * SECONDS_PER_DAY);
        List<PlayerStatisticsSnapshot> snapshots = new ArrayList<>();
        for (int dayOffset = 0; dayOffset < 90; dayOffset++) {
            snapshots.add(snapshot(1, "all", (NOW_DAY - dayOffset) * SECONDS_PER_DAY));
        }

        RetentionPlan plan = service.plan(snapshots, now);

        Map<Granularity, Long> keptByGranularity = plan.kept().stream()
                .collect(Collectors.groupingBy(PlayerStatisticsSnapshot::getGranularity, Collectors.counting()));

        assertEquals(30L, keptByGranularity.get(Granularity.DAILY), "30 daily rows for the last 30 days");
        assertEquals(8L, keptByGranularity.get(Granularity.WEEKLY), "8 weekly rows for days 30-85");
        assertEquals(1L, keptByGranularity.get(Granularity.MONTHLY), "1 monthly row for the oldest days");
        assertEquals(39, plan.kept().size());
        assertEquals(51, plan.toDelete().size(), "90 inputs minus 39 kept");
    }

    @Test
    public void keepsTheMostRecentSnapshotWithinEachCollapsedBucket() {
        Instant now = Instant.ofEpochSecond(NOW_DAY * SECONDS_PER_DAY);
        // Two snapshots inside the same weekly bucket. Weekly buckets are 7-day windows
        // measured back from the daily cutoff (day -30), so ages 31 and 34 both fall in
        // the first weekly bucket.
        PlayerStatisticsSnapshot older = snapshot(1, "all", (NOW_DAY - 34) * SECONDS_PER_DAY);
        PlayerStatisticsSnapshot newer = snapshot(1, "all", (NOW_DAY - 31) * SECONDS_PER_DAY);

        RetentionPlan plan = service.plan(List.of(older, newer), now);

        assertEquals(1, plan.kept().size());
        assertEquals(newer.getCreateTimestamp(), plan.kept().get(0).getCreateTimestamp());
        assertEquals(Granularity.WEEKLY, plan.kept().get(0).getGranularity());
        assertEquals(List.of(older), plan.toDelete());
    }

    @Test
    public void bucketsEachAccountAndGameModeSeriesIndependently() {
        Instant now = Instant.ofEpochSecond(NOW_DAY * SECONDS_PER_DAY);
        // Same day, but different account/game-mode combinations must not collapse together.
        long ts = (NOW_DAY - 1) * SECONDS_PER_DAY;
        List<PlayerStatisticsSnapshot> snapshots = List.of(
                snapshot(1, "all", ts),
                snapshot(2, "all", ts),
                snapshot(1, "random", ts)
        );

        RetentionPlan plan = service.plan(snapshots, now);

        assertEquals(3, plan.kept().size());
        assertEquals(0, plan.toDelete().size());
    }

    private static PlayerStatisticsSnapshot snapshot(int accountId, String gameMode, long createTimestamp) {
        PlayerStatisticsSnapshot snapshot = new PlayerStatisticsSnapshot();
        snapshot.setAccountId(accountId);
        snapshot.setGameMode(gameMode);
        snapshot.setCreateTimestamp(createTimestamp);
        return snapshot;
    }
}
