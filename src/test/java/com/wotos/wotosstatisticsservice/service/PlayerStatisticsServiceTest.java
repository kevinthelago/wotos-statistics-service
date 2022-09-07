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

import java.util.*;

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

    // ToDo: Make env variables better
    @Value("${env.snapshot_rate}")
    private Integer SNAPSHOT_RATE;
    @Value("${env.app_id}")
    private String APP_ID;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(playerStatisticsService, "SNAPSHOT_RATE", SNAPSHOT_RATE);
        ReflectionTestUtils.setField(playerStatisticsService, "APP_ID", APP_ID);
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
        PlayerStatisticsSnapshot playerStatisticsSnapshot = buildRandomPlayerStatisticsSnapshot(1);
        playerStatisticsSnapshotsList.add(playerStatisticsSnapshot);
        when(playerStatisticsSnapshotsRepository.save(any(PlayerStatisticsSnapshot.class))).thenReturn(playerStatisticsSnapshot);

        when(playerStatisticsSnapshotsRepository.findByAccountIdAndGameMode(1, "all")).thenReturn(Optional.of(playerStatisticsSnapshotsList));

        List<Integer> accountIds = new ArrayList<>();
        accountIds.add(1);
        playerStatisticsService.getPlayerStatisticsSnapshots(accountIds);

//        verify(playerStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountId(1);
//        verify(playerStatisticsSnapshotsRepository, times(1)).save(any(PlayerStatisticsSnapshot.class));
//        verify(playerStatisticsSnapshotsRepository, times(1)).findByAccountId(1);
    }

    private WotPlayerDetails buildRandomPlayerDetails(Integer accountId, Integer battles) {
        return new WotPlayerDetails(
                "", 0, 0,0,
                0,false,0,0,
                buildRandomPlayerStatistics(battles), "", 0
        );
    }

    private WotPlayerStatistics buildRandomPlayerStatistics(Integer battles) {
        return new WotPlayerStatistics(
                null,
                buildRandomStatisticsByGameMode(battles),
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                0
        );
    }

    private WotStatisticsByGameMode buildRandomStatisticsByGameMode(Integer battles) {
        return new WotStatisticsByGameMode(
                0,battles, 0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,
                0f,0f,0f,0,0,0,0,0f,0
        );
    }

    private PlayerStatisticsSnapshot buildRandomPlayerStatisticsSnapshot(Integer accountId) {
        PlayerStatisticsSnapshot playerStatisticsSnapshot = new PlayerStatisticsSnapshot();

        playerStatisticsSnapshot.setPlayerStatisticsSnapshotId(0);
        playerStatisticsSnapshot.setAccountId(accountId);
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
