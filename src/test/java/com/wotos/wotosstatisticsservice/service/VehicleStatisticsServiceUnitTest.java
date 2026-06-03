package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.client.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.client.wot.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.client.wot.statistics.WotStatistics;
import com.wotos.wotosstatisticsservice.client.wot.statistics.WotVehicleStatistics;
import com.wotos.wotosstatisticsservice.client.xvm.XvmExpectedStatisticsFeignClient;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure (no Spring context / database) unit tests for {@link VehicleStatisticsService},
 * focused on the snapshot-creation edge cases called out in issue #10.
 */
@ExtendWith(MockitoExtension.class)
public class VehicleStatisticsServiceUnitTest {

    @Mock
    private WotPlayerVehiclesFeignClient wotPlayerVehiclesFeignClient;
    @Mock
    private XvmExpectedStatisticsFeignClient xvmExpectedStatisticsFeignClient;
    @Mock
    private VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    @Mock
    private ExpectedStatisticsRepository expectedStatisticsRepository;

    private VehicleStatisticsService service;

    @BeforeEach
    public void setUp() {
        service = new VehicleStatisticsService(
                wotPlayerVehiclesFeignClient,
                xvmExpectedStatisticsFeignClient,
                vehicleStatisticsSnapshotsRepository,
                expectedStatisticsRepository,
                new Wn8Calculator()
        );
        ReflectionTestUtils.setField(service, "SNAPSHOT_RATE", 10);
        ReflectionTestUtils.setField(service, "APP_ID", "");
    }

    /**
     * Issue #10 edge case: when XVM publishes no expected statistics for a vehicle,
     * that vehicle must be excluded from the snapshot rather than crashing the call.
     */
    @Test
    public void missingExpectedStatisticsExcludesVehicleWithoutCrashingOrSaving() {
        stubVehicleResponse(vehicleStats(1, wot(100, 50, 100, 100, 150_000, 100)));
        when(vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(1, 1, "all"))
                .thenReturn(Optional.of(0));
        when(expectedStatisticsRepository.findById(1)).thenReturn(Optional.empty());

        Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> result =
                service.createPlayerVehicleStatisticsSnapshots(new Integer[]{1}, new Integer[]{1});

        // Vehicle is present in the result structure but contributes no snapshots.
        assertTrue(result.get(1).get(1).isEmpty(), "vehicle without expected stats should be excluded");
        verify(vehicleStatisticsSnapshotsRepository, never()).save(any());
    }

    /** Happy path: with expected statistics present a snapshot is computed and saved. */
    @Test
    public void createsAndSavesSnapshotWhenExpectedStatisticsPresent() {
        stubVehicleResponse(vehicleStats(1, wot(100, 50, 100, 100, 150_000, 100)));
        when(vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(1, 1, "all"))
                .thenReturn(Optional.of(0));
        when(expectedStatisticsRepository.findById(1)).thenReturn(Optional.of(expected(1500f, 1f, 1f, 1f, 50f)));

        Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> result =
                service.createPlayerVehicleStatisticsSnapshots(new Integer[]{1}, new Integer[]{1});

        VehicleStatisticsSnapshot snapshot = result.get(1).get(1).get("all");
        assertEquals(1565f, snapshot.getAverageWn8(), 1.0f);
        assertEquals(100, snapshot.getTotalBattles());
        verify(vehicleStatisticsSnapshotsRepository).save(any(VehicleStatisticsSnapshot.class));
    }

    /** The read path delegates straight to the repository's grouping helper. */
    @Test
    public void getPlayerVehicleStatisticsSnapshotsMapDelegatesToRepository() {
        Integer[] accountIds = {1};
        Integer[] vehicleIds = {1};
        String[] gameModes = {"all"};
        Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>> expected = Map.of();
        when(vehicleStatisticsSnapshotsRepository.findAllPlayerVehicleStatisticsMapByAccountId(accountIds, vehicleIds, gameModes))
                .thenReturn(expected);

        assertEquals(expected, service.getPlayerVehicleStatisticsSnapshotsMap(accountIds, vehicleIds, gameModes));
    }

    private void stubVehicleResponse(WotVehicleStatistics vehicle) {
        WotApiResponse<Map<Integer, List<WotVehicleStatistics>>> body =
                new WotApiResponse<>("", "", "", Map.of(1, List.of(vehicle)));
        when(wotPlayerVehiclesFeignClient.getPlayerVehicleStatistics(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
    }

    /** A vehicle-stats record carrying {@code allStats} for the "all" game mode and empty elsewhere. */
    private static WotVehicleStatistics vehicleStats(int vehicleId, WotStatistics allStats) {
        WotStatistics empty = wot(0, 0, 0, 0, 0, 0);
        return new WotVehicleStatistics(
                0, vehicleId, null, 0, 0, 0, 1,
                empty, empty, empty, empty,
                allStats, empty, empty, empty,
                empty, empty, empty, empty,
                empty
        );
    }

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
