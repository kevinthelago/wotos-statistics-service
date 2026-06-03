package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.client.wot.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.dto.PlayerTrendResponse;
import com.wotos.wotosstatisticsservice.dto.TrendPoint;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link PlayerStatisticsService#getPlayerWn8Trend}, covering
 * day/week bucketing, latest-per-bucket selection, and ISO-8601 UTC formatting.
 */
@ExtendWith(MockitoExtension.class)
public class PlayerStatisticsServiceTrendTest {

    private static final long SECONDS_PER_DAY = 86_400L;
    private static final long DAY_19000 = 19_000L * SECONDS_PER_DAY; // 2022-01-08T00:00:00Z
    private static final long DAY_19001 = 19_001L * SECONDS_PER_DAY; // 2022-01-09T00:00:00Z

    @Mock
    private WotAccountsFeignClient wotAccountsFeignClient;
    @Mock
    private VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    @Mock
    private PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository;

    private PlayerStatisticsService service;

    @BeforeEach
    public void setUp() {
        service = new PlayerStatisticsService(
                wotAccountsFeignClient,
                vehicleStatisticsSnapshotsRepository,
                playerStatisticsSnapshotsRepository
        );
    }

    @Test
    public void dayBucketKeepsLatestSnapshotPerDayWithIsoUtcTimestamps() {
        // Two snapshots on 2022-01-08 (01:00 and 02:00) and one on 2022-01-09.
        List<PlayerStatisticsSnapshot> snapshots = List.of(
                snapshot(DAY_19000 + 3600, 1500f, 100),
                snapshot(DAY_19000 + 7200, 1550f, 110),
                snapshot(DAY_19001 + 3600, 1600f, 120)
        );
        when(playerStatisticsSnapshotsRepository
                .findAllByAccountIdAndGameModeAndCreateTimestampBetweenOrderByCreateTimestampAsc(eq(1), eq("all"), anyLong(), anyLong()))
                .thenReturn(snapshots);

        PlayerTrendResponse response = service.getPlayerWn8Trend(1, null, null, "day");

        assertEquals(1, response.accountId());
        assertEquals("day", response.bucket());
        assertEquals(2, response.points().size());

        TrendPoint first = response.points().get(0);
        assertEquals("2022-01-08T00:00:00Z", first.t());
        assertEquals(1550f, first.wn8()); // 02:00 snapshot wins the day bucket
        assertEquals(110, first.battles());

        TrendPoint second = response.points().get(1);
        assertEquals("2022-01-09T00:00:00Z", second.t());
        assertEquals(1600f, second.wn8());
    }

    @Test
    public void weekBucketCollapsesSameIsoWeekToTheLatestSnapshot() {
        // 2022-01-08 (Sat) and 2022-01-09 (Sun) share the ISO week starting Mon 2022-01-03.
        List<PlayerStatisticsSnapshot> snapshots = List.of(
                snapshot(DAY_19000 + 3600, 1500f, 100),
                snapshot(DAY_19001 + 3600, 1600f, 120)
        );
        when(playerStatisticsSnapshotsRepository
                .findAllByAccountIdAndGameModeAndCreateTimestampBetweenOrderByCreateTimestampAsc(eq(1), eq("all"), anyLong(), anyLong()))
                .thenReturn(snapshots);

        PlayerTrendResponse response = service.getPlayerWn8Trend(1, null, null, "week");

        assertEquals("week", response.bucket());
        assertEquals(1, response.points().size());
        TrendPoint point = response.points().get(0);
        assertEquals("2022-01-03T00:00:00Z", point.t());
        assertEquals(1600f, point.wn8()); // latest snapshot in the week
    }

    @Test
    public void returnsEmptyPointsWhenNoSnapshots() {
        when(playerStatisticsSnapshotsRepository
                .findAllByAccountIdAndGameModeAndCreateTimestampBetweenOrderByCreateTimestampAsc(eq(1), eq("all"), anyLong(), anyLong()))
                .thenReturn(List.of());

        PlayerTrendResponse response = service.getPlayerWn8Trend(1, null, null, "day");

        assertEquals(0, response.points().size());
    }

    private static PlayerStatisticsSnapshot snapshot(long createTimestamp, float wn8, int battles) {
        PlayerStatisticsSnapshot snapshot = new PlayerStatisticsSnapshot();
        snapshot.setAccountId(1);
        snapshot.setGameMode("all");
        snapshot.setCreateTimestamp(createTimestamp);
        snapshot.setTotalAverageWn8(wn8);
        snapshot.setTotalBattles(battles);
        return snapshot;
    }
}
