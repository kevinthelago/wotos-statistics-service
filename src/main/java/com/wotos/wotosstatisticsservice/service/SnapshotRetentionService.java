package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.dao.Granularity;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collapses the player statistics snapshot time-series so it does not grow without
 * bound. Each {@code (accountId, gameMode)} series is thinned by age:
 *
 * <ul>
 *   <li>newer than {@code dailyWindowDays} (default 30) — kept at {@link Granularity#DAILY}
 *       resolution (one snapshot per day);</li>
 *   <li>between {@code dailyWindowDays} and {@code weeklyWindowDays} (default 365) —
 *       thinned to {@link Granularity#WEEKLY} (one per 7-day bucket);</li>
 *   <li>older than {@code weeklyWindowDays} — thinned to {@link Granularity#MONTHLY}
 *       (one per 30-day bucket) and kept forever.</li>
 * </ul>
 *
 * <p>Within each bucket the most recent snapshot is kept (its granularity is
 * stamped) and the rest are deleted. Buckets are measured in whole days relative to
 * each zone's cutoff so the outcome is deterministic and independent of calendar
 * alignment.
 */
@Service
public class SnapshotRetentionService {

    private static final long SECONDS_PER_DAY = 86_400L;

    @Value("${env.retention.daily_days:30}")
    private int dailyWindowDays;
    @Value("${env.retention.weekly_days:365}")
    private int weeklyWindowDays;

    private final PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository;

    public SnapshotRetentionService(PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository) {
        this.playerStatisticsSnapshotsRepository = playerStatisticsSnapshotsRepository;
    }

    /**
     * Scheduled daily (04:00 by default) collapse of the whole snapshot table.
     * Stamps the surviving snapshots with their new granularity and deletes the rest.
     */
    @Scheduled(cron = "${env.retention.cron:0 0 4 * * *}")
    public void collapseSnapshots() {
        applyRetention(Instant.now());
    }

    /**
     * Runs the retention collapse against the persisted snapshots as of {@code now},
     * persisting the surviving granularities and removing the superseded rows.
     *
     * @param now the reference instant ages are measured from
     * @return the plan that was applied
     */
    public RetentionPlan applyRetention(Instant now) {
        RetentionPlan plan = plan(playerStatisticsSnapshotsRepository.findAll(), now);
        playerStatisticsSnapshotsRepository.saveAll(plan.kept());
        playerStatisticsSnapshotsRepository.deleteAllInBatch(plan.toDelete());
        return plan;
    }

    /**
     * Pure (no I/O) computation of which snapshots survive the collapse and at what
     * granularity, and which are superseded. Each surviving snapshot returned in
     * {@link RetentionPlan#kept()} has had its granularity set.
     *
     * @param snapshots the full set of snapshots to consider
     * @param now       the reference instant ages are measured from
     * @return the retention plan
     */
    public RetentionPlan plan(List<PlayerStatisticsSnapshot> snapshots, Instant now) {
        long nowDay = Math.floorDiv(now.getEpochSecond(), SECONDS_PER_DAY);
        long dailyCutoffDay = nowDay - dailyWindowDays;
        long weeklyCutoffDay = nowDay - weeklyWindowDays;

        List<Classified> classifieds = new ArrayList<>(snapshots.size());
        Map<String, PlayerStatisticsSnapshot> winnerByBucket = new HashMap<>();

        for (PlayerStatisticsSnapshot snapshot : snapshots) {
            long day = Math.floorDiv(snapshot.getCreateTimestamp(), SECONDS_PER_DAY);

            Granularity granularity;
            long bucket;
            if (day > dailyCutoffDay) {
                granularity = Granularity.DAILY;
                bucket = day;
            } else if (day > weeklyCutoffDay) {
                granularity = Granularity.WEEKLY;
                bucket = Math.floorDiv(dailyCutoffDay - day, 7L);
            } else {
                granularity = Granularity.MONTHLY;
                bucket = Math.floorDiv(weeklyCutoffDay - day, 30L);
            }

            String key = snapshot.getAccountId() + "|" + snapshot.getGameMode() + "|" + granularity + "|" + bucket;
            classifieds.add(new Classified(snapshot, key, granularity));
            winnerByBucket.merge(key, snapshot,
                    (existing, candidate) -> candidate.getCreateTimestamp() >= existing.getCreateTimestamp() ? candidate : existing);
        }

        List<PlayerStatisticsSnapshot> kept = new ArrayList<>();
        List<PlayerStatisticsSnapshot> toDelete = new ArrayList<>();
        for (Classified classified : classifieds) {
            if (winnerByBucket.get(classified.key()) == classified.snapshot()) {
                classified.snapshot().setGranularity(classified.granularity());
                kept.add(classified.snapshot());
            } else {
                toDelete.add(classified.snapshot());
            }
        }

        return new RetentionPlan(kept, toDelete);
    }

    /** A snapshot paired with the retention bucket it falls into. */
    private record Classified(PlayerStatisticsSnapshot snapshot, String key, Granularity granularity) {
    }

    /**
     * The outcome of a retention pass.
     *
     * @param kept     surviving snapshots, each stamped with its new granularity
     * @param toDelete superseded snapshots to remove
     */
    public record RetentionPlan(List<PlayerStatisticsSnapshot> kept, List<PlayerStatisticsSnapshot> toDelete) {
    }
}
