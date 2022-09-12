package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotPlayerStatistics;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatisticsByGameMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.Column;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.samePropertyValuesAs;
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

//    // ToDo: Make env variables better
//    @Value("${env.snapshot_rate}")
//    private Integer SNAPSHOT_RATE;
//    @Value("${env.app_id}")
//    private String APP_ID;

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

        WotApiResponse<Map<Integer, WotPlayerDetails>> wotApiResponsePlayer1 = new WotApiResponse<>("", "", "", wotPlayerDetailsMapPlayer1);
        ResponseEntity<WotApiResponse<Map<Integer, WotPlayerDetails>>> wotResponseEntityPlayer1 = new ResponseEntity<>(wotApiResponsePlayer1, HttpStatus.OK);
        when(wotAccountsFeignClient.getPlayerDetails("", "", "", "", "", 1)).thenReturn(wotResponseEntityPlayer1);

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

        List<Integer> accountIds = new ArrayList<>();
        accountIds.add(1);
        playerStatisticsService.createPlayerStatisticsSnapshotsByAccountIds(accountIds);

        verify(wotAccountsFeignClient, times(1)).getPlayerDetails("", "", "", "", "", 1);
        verify(playerStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndGameMode(1, "all");
        verify(playerStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndGameMode(1, "clan");
        verify(vehicleStatisticsSnapshotsRepository, times(1)).averageAverageWn8ByGameModeAndAccountId(1, "all");
        verify(vehicleStatisticsSnapshotsRepository, times(1)).averageAverageWn8ByGameModeAndAccountId(1, "clan");
        verify(playerStatisticsSnapshotsRepository, times(2)).save(any(PlayerStatisticsSnapshot.class));
    }

    @Test
    public void getPlayerStatisticsSnapshotsTest() {
        // ToDo: Assure calculations are correct rather than building with random values
        Map<Integer, WotPlayerDetails> playerDetailsMap = new HashMap<>();
        playerDetailsMap.put(1, buildRandomPlayerDetails(1, 11));

        WotApiResponse<Map<Integer, WotPlayerDetails>> wotApiResponse = new WotApiResponse("", "", "", playerDetailsMap);

        ResponseEntity<WotApiResponse<Map<Integer, WotPlayerDetails>>> wotResponseEntity = new ResponseEntity(wotApiResponse, HttpStatus.FOUND);
        when(wotAccountsFeignClient.getPlayerDetails(
                "", "", "", "", "", 1)
        ).thenReturn(wotResponseEntity);

        Optional<Integer> maxBattles = Optional.of(0);
        when(playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(1, "all")).thenReturn(maxBattles);

        List<PlayerStatisticsSnapshot> playerStatisticsSnapshotsList = new ArrayList<>();
        PlayerStatisticsSnapshot playerStatisticsSnapshot = buildPlayerStatisticsSnapshot(1);
        playerStatisticsSnapshotsList.add(playerStatisticsSnapshot);
        when(playerStatisticsSnapshotsRepository.save(any(PlayerStatisticsSnapshot.class))).thenReturn(playerStatisticsSnapshot);

        when(playerStatisticsSnapshotsRepository.findByAccountIdAndGameMode(1, "all")).thenReturn(Optional.of(playerStatisticsSnapshotsList));

        List<Integer> accountIds = new ArrayList<>();
        accountIds.add(1);
        List<String> gameModes = new ArrayList<>();
        gameModes.add("all");
        playerStatisticsService.getPlayerStatisticsSnapshotsMap(accountIds, gameModes);

        verify(playerStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndGameMode(1, "all");
        verify(playerStatisticsSnapshotsRepository, times(1)).save(any(PlayerStatisticsSnapshot.class));
        verify(playerStatisticsSnapshotsRepository, times(1)).findByAccountIdAndGameMode(1, "all");
    }

    private WotPlayerDetails buildRandomPlayerDetails(Integer accountId, Integer battles) {
        return new WotPlayerDetails(
                "", 0, accountId,0,
                0,false,0,0,
                buildPlayerStatistics(battles), "", 0
        );
    }

    private WotPlayerStatistics buildPlayerStatistics(Integer battles) {
        return new WotPlayerStatistics(
                buildRandomStatisticsByGameMode(0),
                buildRandomStatisticsByGameMode(battles),
                buildRandomStatisticsByGameMode(0),
                0,
                buildRandomStatisticsByGameMode(0),
                buildRandomStatisticsByGameMode(0),
                buildRandomStatisticsByGameMode(0),
                buildRandomStatisticsByGameMode(0),
                buildRandomStatisticsByGameMode(0),
                0
        );
    }

    private WotStatisticsByGameMode buildRandomStatisticsByGameMode(Integer battles) {
        return new WotStatisticsByGameMode(
                0,battles, 0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,
                0f,0f,0f,0,0,0,0,0f,0f
        );
    }

    private PlayerStatisticsSnapshot buildPlayerStatisticsSnapshot(Integer accountId) {
        PlayerStatisticsSnapshot playerStatisticsSnapshot = new PlayerStatisticsSnapshot();

        playerStatisticsSnapshot.setPlayerStatisticsSnapshotId(0);
        playerStatisticsSnapshot.setAccountId(accountId);
        playerStatisticsSnapshot.setGameMode("all");
        playerStatisticsSnapshot.setCreateTimestamp(Instant.now().getEpochSecond());
        playerStatisticsSnapshot.setTotalBattles(0);
        playerStatisticsSnapshot.setSurvivedBattles(0);
        playerStatisticsSnapshot.setKillDeathRatio(0.0f);
        playerStatisticsSnapshot.setHitMissRatio(0.0f);
        playerStatisticsSnapshot.setWinLossRatio(0.0f);
        playerStatisticsSnapshot.setTotalAverageWn8(0.0f);
        playerStatisticsSnapshot.setAverageExperience(0.0f);
        playerStatisticsSnapshot.setAverageDamage(0.0f);
        playerStatisticsSnapshot.setAverageKills(0.0f);
        playerStatisticsSnapshot.setAverageDamageReceived(0.0f);
        playerStatisticsSnapshot.setAverageShots(0.0f);
        playerStatisticsSnapshot.setAverageStunAssistedDamage(0.0f);
        playerStatisticsSnapshot.setAverageCapturePoints(0.0f);
        playerStatisticsSnapshot.setAverageDroppedCapturePoints(0.0f);
        playerStatisticsSnapshot.setAverageSpotting(0.0f);

        return playerStatisticsSnapshot;
    }

}
