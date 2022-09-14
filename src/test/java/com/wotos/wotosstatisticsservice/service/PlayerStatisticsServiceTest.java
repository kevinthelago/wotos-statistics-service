package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.wot.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.wot.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotPlayerStatistics;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatisticsByGameMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PlayerStatisticsServiceTest {

    private final Random rng = new Random();
    @Autowired
    @InjectMocks
    PlayerStatisticsService playerStatisticsService;
    @Autowired
    @MockBean
    private VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    @Autowired
    @MockBean
    private PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository;
    @Autowired
    @MockBean
    private WotAccountsFeignClient wotAccountsFeignClient;
    @Autowired
    @MockBean
    private WotPlayerVehiclesFeignClient wotPlayerVehiclesFeignClient;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(playerStatisticsService, "SNAPSHOT_RATE", 10);
        ReflectionTestUtils.setField(playerStatisticsService, "APP_ID", "");
    }

    @Test
    public void createPlayerStatisticsSnapshotTest() {
        WotStatisticsByGameMode wotAllStatistics = new WotStatisticsByGameMode(
                22311, 16815, 9004,248,7563,16871,10625800,159323,
                66317,109538,20840,71468,3136,
                6172,48870,231,2664,
                4856,9267,69,16311407,1492,
                15736,157591,448.61f,
                74.71f, 373.9f,8622,632,
                20874970,10,0.38f,390.32f
        );

        WotStatisticsByGameMode wotClanStatistics = new WotStatisticsByGameMode(
                169,143,86,14,43,107,104847,974,484,766,188,638,4,49,
                450,0,0,63,86,79,235116,0,102,0,
                573.63f,165f,408.63f,0,733,251883,0,0.47f,642.21f
        );

        WotStatisticsByGameMode wotEmptyStatistics = new WotStatisticsByGameMode(
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0f,0f,0f,0,0,0,0,0f,0f
        );

        WotPlayerStatistics wotPlayerStatistics = new WotPlayerStatistics(
                wotClanStatistics, wotAllStatistics, wotEmptyStatistics, 0, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, null
        );

        WotPlayerDetails wotPlayerDetailsPlayer1 = new WotPlayerDetails(
                "", 0, 1,0,
                0,false,0,0,
                wotPlayerStatistics, "", 0
        );

//        WotPlayerDetails wotPlayerDetailsPlayer2 = new WotPlayerDetails(
//                "", 0, 2,0,
//                0,false,0,0,
//                wotPlayerStatistics, "", 0
//        );

        Map<Integer, WotPlayerDetails> wotPlayerDetailsMapPlayer1 = new HashMap<>();
        wotPlayerDetailsMapPlayer1.put(1, wotPlayerDetailsPlayer1);
        Integer[] accountIds = {1};

        WotApiResponse<Map<Integer, WotPlayerDetails>> wotApiResponsePlayer1 = new WotApiResponse<>("", "", "", wotPlayerDetailsMapPlayer1);
        ResponseEntity<WotApiResponse<Map<Integer, WotPlayerDetails>>> wotResponseEntityPlayer1 = new ResponseEntity<>(wotApiResponsePlayer1, HttpStatus.OK);
        when(wotAccountsFeignClient.getPlayerDetails("", "", null, null, "", accountIds)).thenReturn(wotResponseEntityPlayer1);

        when(playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(1, "all")).thenReturn(Optional.of(0));
        when(vehicleStatisticsSnapshotsRepository.averageAverageWn8ByGameModeAndAccountId(1, "all")).thenReturn(Optional.of(1592.67f));

        PlayerStatisticsSnapshot expectedPlayerStatisticsSnapshotPlayer1 = new PlayerStatisticsSnapshot();

        expectedPlayerStatisticsSnapshotPlayer1.setPlayerStatisticsSnapshotId(1);
        expectedPlayerStatisticsSnapshotPlayer1.setAccountId(1);
        expectedPlayerStatisticsSnapshotPlayer1.setGameMode("all");
        expectedPlayerStatisticsSnapshotPlayer1.setCreateTimestamp(Instant.now().getEpochSecond());
        expectedPlayerStatisticsSnapshotPlayer1.setTotalBattles(16815);
        expectedPlayerStatisticsSnapshotPlayer1.setSurvivedBattles(4856);
        expectedPlayerStatisticsSnapshotPlayer1.setKillDeathRatio(1.41074f);
        expectedPlayerStatisticsSnapshotPlayer1.setHitMissRatio(0.687522f);
        expectedPlayerStatisticsSnapshotPlayer1.setWinLossRatio(0.535474f);
        expectedPlayerStatisticsSnapshotPlayer1.setTotalAverageWn8(1592.67f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageExperience(631.924f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageDamage(1241.45f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageKills(1.00333f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageDamageReceived(970.051f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageShots(9.47505f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageStunAssistedDamage(9.37205f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageCapturePoints(0.935831f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageDroppedCapturePoints(0.551115f);
        expectedPlayerStatisticsSnapshotPlayer1.setAverageSpotting(1.32685f);

        Map<Integer, Map<String, PlayerStatisticsSnapshot>> playerStatisticsSnapshotsMap = playerStatisticsService.createPlayerStatisticsSnapshotsByAccountIds(accountIds);

        verify(wotAccountsFeignClient, times(1)).getPlayerDetails("", "", null, null, "", accountIds);
        verify(playerStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndGameMode(1, "all");
        verify(playerStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndGameMode(1, "clan");
        verify(vehicleStatisticsSnapshotsRepository, times(1)).averageAverageWn8ByGameModeAndAccountId(1, "all");
        verify(vehicleStatisticsSnapshotsRepository, times(1)).averageAverageWn8ByGameModeAndAccountId(1, "clan");
        verify(playerStatisticsSnapshotsRepository, times(2)).save(any(PlayerStatisticsSnapshot.class));
    }

    @Test
    public void getPlayerStatisticsSnapshotsTest() {

    }

}
