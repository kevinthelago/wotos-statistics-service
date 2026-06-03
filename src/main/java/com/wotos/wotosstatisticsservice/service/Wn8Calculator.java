package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.client.wot.statistics.WotStatistics;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import org.springframework.stereotype.Component;

/**
 * Calculates a vehicle's WN8 rating from raw WoT statistics and the XVM expected
 * values for that vehicle.
 *
 * <p>The implementation follows the canonical WN8 formula published by the WN8
 * working group (the same algorithm used by XVM / wnefficiency). Reference:
 * <a href="https://wiki.wnefficiency.net/pages/WN8">https://wiki.wnefficiency.net/pages/WN8</a>
 * (mirror of the original wnefficiency.net specification). The five "step 1" ratios
 * compare the player's per-battle averages against the vehicle's expected averages;
 * the "step 2" coefficients normalise and cap those ratios; the final weighted sum
 * uses the published constants 980 / 210 / 155 / 75 / 145.
 *
 * <p>Sanity check baked into the test suite: a player whose every ratio equals the
 * expected value (all step-1 ratios = 1.0) scores exactly <b>1565</b> WN8.
 */
@Component
public class Wn8Calculator {

    /** Rounding granularity applied to each step-1 ratio (4 decimal places). */
    private static final float TUNE = 10000f;

    /**
     * Computes the WN8 rating for a single vehicle in a single game mode.
     *
     * @param wotStatistics      raw cumulative statistics for the vehicle (battles, damage, frags, ...)
     * @param expectedStatistics XVM expected values for the same vehicle; must not be {@code null}
     * @return the WN8 rating, or {@code 0f} when the vehicle has no battles (no data to rate)
     */
    public float calculateWn8(WotStatistics wotStatistics, ExpectedStatistics expectedStatistics) {
        float battles = wotStatistics.getBattles();

        // Edge case: a vehicle with zero battles has no per-battle averages to rate.
        // Returning 0 avoids division-by-zero producing NaN/Infinity downstream.
        if (battles <= 0) {
            return 0f;
        }

        float wins = wotStatistics.getWins();
        float frags = wotStatistics.getFrags();
        float spotted = wotStatistics.getSpotted();
        float damage = wotStatistics.getDamageDealt();
        float droppedCapturePoints = wotStatistics.getDroppedCapturePoints();

        float winLossRatio = wins / battles;
        float averageKillsPerGame = frags / battles;
        float averageSpottingPerGame = spotted / battles;
        float averageDamagePerGame = damage / battles;
        float averageDroppedCapturePoints = droppedCapturePoints / battles;

        // Step 1 — ratio of actual to expected, rounded to 4 decimal places.
        // Expected win rate is a percentage (e.g. 49.0), so winLossRatio (0..1) is
        // scaled by an extra 100 to compare like-for-like.
        float DAMAGE = Math.round((averageDamagePerGame / expectedStatistics.getExpectedDamage()) * TUNE) / TUNE;
        float SPOT = Math.round((averageSpottingPerGame / expectedStatistics.getExpectedSpot()) * TUNE) / TUNE;
        float FRAG = Math.round((averageKillsPerGame / expectedStatistics.getExpectedFrag()) * TUNE) / TUNE;
        float DEFENSE = Math.round((averageDroppedCapturePoints / expectedStatistics.getExpectedDefense()) * TUNE) / TUNE;
        float WIN = Math.round((winLossRatio / expectedStatistics.getExpectedWinRate()) * (TUNE * 100)) / TUNE;

        // Step 2 — normalise and cap each ratio.
        float DAMAGEc = (float) Math.max(0, (DAMAGE - 0.22) / 0.78);
        float SPOTc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (SPOT - 0.38) / 0.62));
        float FRAGc = (float) Math.max(0, Math.min(DAMAGEc + 0.2, (FRAG - 0.12) / 0.88));
        float DEFENSEc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (DEFENSE - 0.10) / 0.9));
        float WINc = (float) Math.max(0, (WIN - 0.71) / 0.29);

        // Step 3 — weighted sum with the published WN8 constants.
        return (float) ((980 * DAMAGEc)
                + (210 * DAMAGEc * FRAGc)
                + (155 * FRAGc * SPOTc)
                + (75 * DEFENSEc * FRAGc)
                + (145 * Math.min(1.8, WINc)));
    }
}
