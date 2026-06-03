package com.wotos.wotosstatisticsservice.config;

import com.wotos.wotosstatisticsservice.service.SnapshotRetentionService;
import com.wotos.wotosstatisticsservice.service.SnapshotRetentionService.RetentionPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * One-shot retention collapse over the existing (historical) snapshot data.
 *
 * <p>Active only under the {@code backfill} profile — start the service with
 * {@code --spring.profiles.active=backfill} (or {@code -Dspring.profiles.active=backfill})
 * to thin already-persisted snapshots that predate the scheduled job, then run
 * normally.
 */
@Component
@Profile("backfill")
public class SnapshotBackfillRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SnapshotBackfillRunner.class);

    private final SnapshotRetentionService snapshotRetentionService;

    public SnapshotBackfillRunner(SnapshotRetentionService snapshotRetentionService) {
        this.snapshotRetentionService = snapshotRetentionService;
    }

    @Override
    public void run(String... args) {
        log.info("Backfill profile active — collapsing historical snapshots.");
        RetentionPlan plan = snapshotRetentionService.applyRetention(Instant.now());
        log.info("Backfill retention complete: kept {} snapshots, deleted {}.",
                plan.kept().size(), plan.toDelete().size());
    }
}
