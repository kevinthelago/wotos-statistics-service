package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.client.wot.statistics.WotStatistics;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link Wn8Calculator}, verifying the implementation against the
 * canonical WN8 reference formula.
 *
 * <p><b>Reference / source of the test vectors:</b> the WN8 specification published
 * by the WN8 working group, mirrored at
 * <a href="https://wiki.wnefficiency.net/pages/WN8">https://wiki.wnefficiency.net/pages/WN8</a>
 * (originally wnefficiency.net). The specification's well-known sanity check is that
 * a player whose every step-1 ratio equals the expected value (rDAMAGE = rSPOT =
 * rFRAG = rDEF = rWIN = 1.0) scores exactly <b>1565</b> WN8. The remaining vectors
 * below are derived by applying the same published step-2 normalisation and step-3
 * weighted sum (constants 980 / 210 / 155 / 75 / 145) to chosen inputs.
 *
 * <p>These are pure unit tests — no Spring context or database is required.
 */
public class Wn8CalculatorTest {

    private final Wn8Calculator wn8Calculator = new Wn8Calculator();

    /**
     * The reference sanity vector: every per-battle average exactly matches the
     * vehicle's expected value, so all step-1 ratios equal 1.0 and WN8 = 1565.
     */
    @Test
    public void allRatiosEqualToExpectedScoresExactly1565() {
        // 100 battles, 50 wins (50% win rate), 100 frags (1.0/battle),
        // 100 spots (1.0/battle), 150000 damage (1500/battle), 100 dropped cap points (1.0/battle).
        WotStatistics raw = wot(100, 50, 100, 100, 150_000, 100);
        // expected: damage 1500, spot 1.0, frag 1.0, defense 1.0, win rate 50%.
        ExpectedStatistics expected = expected(1500f, 1f, 1f, 1f, 50f);

        float wn8 = wn8Calculator.calculateWn8(raw, expected);

        assertEquals(1565f, wn8, 1.0f);
    }

    /**
     * A strong vector: every step-1 ratio = 1.5 (and WINc clamps at 1.8). Expected
     * WN8 ≈ 3015.77 from the published step-2/step-3 math.
     */
    @Test
    public void aboveAverageRatiosScoreAccordingToTheFormula() {
        // 100 battles, 60 wins (60%), 150 frags (1.5/battle), 150 spots (1.5/battle),
        // 150000 damage (1500/battle), 150 dropped cap points (1.5/battle).
        WotStatistics raw = wot(100, 60, 150, 150, 150_000, 150);
        // expected: damage 1000, spot/frag/def 1.0, win rate 40% -> every ratio = 1.5.
        ExpectedStatistics expected = expected(1000f, 1f, 1f, 1f, 40f);

        float wn8 = wn8Calculator.calculateWn8(raw, expected);

        assertEquals(3015.77f, wn8, 2.0f);
    }

    /** Edge case from issue #10: a vehicle with zero battles rates 0, not NaN. */
    @Test
    public void zeroBattlesReturnsZero() {
        WotStatistics raw = wot(0, 0, 0, 0, 0, 0);
        ExpectedStatistics expected = expected(1500f, 1f, 1f, 1f, 50f);

        float wn8 = wn8Calculator.calculateWn8(raw, expected);

        assertEquals(0f, wn8, 0.0f);
    }

    /**
     * A record with battles but no damage, frags, spots, defense or wins drives every
     * normalised coefficient to its zero floor, so the weighted sum is exactly 0.
     */
    @Test
    public void winlessAndDamagelessRecordScoresZero() {
        WotStatistics raw = wot(100, 0, 0, 0, 0, 0);
        ExpectedStatistics expected = expected(1500f, 1f, 1f, 1f, 50f);

        float wn8 = wn8Calculator.calculateWn8(raw, expected);

        assertEquals(0f, wn8, 0.0f);
    }

    /**
     * Builds a {@link WotStatistics} populated with only the fields WN8 depends on;
     * all other counters are zero.
     */
    private static WotStatistics wot(int battles, int wins, int frags, int spotted,
                                     int damageDealt, int droppedCapturePoints) {
        return new WotStatistics(
                spotted, battles, wins, 0,
                0, frags, 0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                droppedCapturePoints, 0, 0,
                0, 0, 0,
                0f, 0f,
                0f, 0, 0,
                damageDealt, 0, 0f,
                0f
        );
    }

    private static ExpectedStatistics expected(float damage, float spot, float frag,
                                               float defense, float winRate) {
        ExpectedStatistics expectedStatistics = new ExpectedStatistics();
        expectedStatistics.setVehicleId(1);
        expectedStatistics.setExpectedDamage(damage);
        expectedStatistics.setExpectedSpot(spot);
        expectedStatistics.setExpectedFrag(frag);
        expectedStatistics.setExpectedDefense(defense);
        expectedStatistics.setExpectedWinRate(winRate);
        return expectedStatistics;
    }
}
