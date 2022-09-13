package com.wotos.wotosstatisticsservice.service;

import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.XvmExpectedStatisticsFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatisticsByGameMode;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotVehicleStatistics;
import com.wotos.wotosstatisticsservice.util.model.xvm.XvmExpectedStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
public class VehicleStatisticsServiceTest {

    private final Random rng = new Random();
    @Autowired
    @InjectMocks
    VehicleStatisticsService vehicleStatisticsService;
    @Autowired
    @MockBean
    private VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    @Autowired
    @MockBean
    private PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository;
    @Autowired
    @MockBean
    private ExpectedStatisticsRepository expectedStatisticsRepository;
    @Autowired
    @MockBean
    private WotAccountsFeignClient wotAccountsFeignClient;
    @Autowired
    @MockBean
    private WotPlayerVehiclesFeignClient wotPlayerVehiclesFeignClient;
    @Autowired
    @MockBean
    private XvmExpectedStatisticsFeignClient xvmExpectedStatisticsFeignClient;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(vehicleStatisticsService, "SNAPSHOT_RATE", 10);
        ReflectionTestUtils.setField(vehicleStatisticsService, "APP_ID", "");
    }

    @Test
    public void createPlayerVehicleStatisticsSnapshotsTest() {
        WotStatisticsByGameMode wotStatisticsByGameMode = new WotStatisticsByGameMode(
                100, 200, 75,50,75,100,100,100,
                50,75,50,100,10,
                50,100,10,100,
                100,1000,75,1000,10,
                100,100,100f,
                100f, 100f,1000,500,
                1000,10,0.50f,100f
        );

        WotStatisticsByGameMode wotEmptyStatistics = new WotStatisticsByGameMode(
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0f,0f,0f,0,0,0,0,0f,0f
        );

        WotVehicleStatistics wotVehicleStatisticsPlayer1Vehicle1 = new WotVehicleStatistics(
                0, 1, null, 0, 0, 0, 1,
                wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics,
                wotStatisticsByGameMode, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics
        );

        WotVehicleStatistics wotVehicleStatisticsPlayer1Vehicle2 = new WotVehicleStatistics(
                0, 2, null, 0, 0, 0, 1,
                wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics,
                wotStatisticsByGameMode, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics
        );

        WotVehicleStatistics wotVehicleStatisticsPlayer1Vehicle3 = new WotVehicleStatistics(
                0, 3, null, 0, 0, 0, 1,
                wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics,
                wotStatisticsByGameMode, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics, wotEmptyStatistics
        );

        List<WotVehicleStatistics> wotVehicleStatisticsList = new ArrayList<>();

        wotVehicleStatisticsList.add(wotVehicleStatisticsPlayer1Vehicle1);
        wotVehicleStatisticsList.add(wotVehicleStatisticsPlayer1Vehicle2);
        wotVehicleStatisticsList.add(wotVehicleStatisticsPlayer1Vehicle3);

        Map<Integer, List<WotVehicleStatistics>> vehicleStatisticsMap = new HashMap<>();
        vehicleStatisticsMap.put(1, wotVehicleStatisticsList);

        WotApiResponse<Map<Integer, List<WotVehicleStatistics>>> wotApiResponse = new WotApiResponse<>("", "", "", vehicleStatisticsMap);
        ResponseEntity<WotApiResponse<Map<Integer, List<WotVehicleStatistics>>>> wotResponseEntity = new ResponseEntity<>(wotApiResponse, HttpStatus.OK);
        Integer[] vehiclesIdsArray = {1,2,3};

        ExpectedStatistics expectedStatisticsVehicle1 = new ExpectedStatistics();
        expectedStatisticsVehicle1.setVehicleId(1);
        expectedStatisticsVehicle1.setExpectedDefense(1f);
        expectedStatisticsVehicle1.setExpectedFrag(1f);
        expectedStatisticsVehicle1.setExpectedSpot(1f);
        expectedStatisticsVehicle1.setExpectedDamage(1f);
        expectedStatisticsVehicle1.setExpectedWinRate(1f);

        ExpectedStatistics expectedStatisticsVehicle2 = new ExpectedStatistics();
        expectedStatisticsVehicle2.setVehicleId(2);
        expectedStatisticsVehicle2.setExpectedDefense(1f);
        expectedStatisticsVehicle2.setExpectedFrag(1f);
        expectedStatisticsVehicle2.setExpectedSpot(1f);
        expectedStatisticsVehicle2.setExpectedDamage(1f);
        expectedStatisticsVehicle2.setExpectedWinRate(1f);

        ExpectedStatistics expectedStatisticsVehicle3 = new ExpectedStatistics();
        expectedStatisticsVehicle3.setVehicleId(3);
        expectedStatisticsVehicle3.setExpectedDefense(1f);
        expectedStatisticsVehicle3.setExpectedFrag(1f);
        expectedStatisticsVehicle3.setExpectedSpot(1f);
        expectedStatisticsVehicle3.setExpectedDamage(1f);
        expectedStatisticsVehicle3.setExpectedWinRate(1f);

        when(wotPlayerVehiclesFeignClient.getPlayerVehicleStatistics("", 1, "", "", "", null, "", vehiclesIdsArray)).thenReturn(wotResponseEntity);
        when(vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(1, 1, "all")).thenReturn(Optional.of(100));
        when(vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(1, 2, "all")).thenReturn(Optional.of(100));
        when(vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(1, 3, "all")).thenReturn(Optional.of(100));
        when(expectedStatisticsRepository.findById(1)).thenReturn(Optional.of(expectedStatisticsVehicle1));
        when(expectedStatisticsRepository.findById(2)).thenReturn(Optional.of(expectedStatisticsVehicle2));
        when(expectedStatisticsRepository.findById(3)).thenReturn(Optional.of(expectedStatisticsVehicle3));

        List<Integer> accountIds = new ArrayList<>();
        accountIds.add(1);
//        accountIds.add(2);
        List<Integer> vehicleIds = new ArrayList<>();
        vehicleIds.add(1);
        vehicleIds.add(2);
        vehicleIds.add(3);

        Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> vehicleStatisticsSnapshotsMap = vehicleStatisticsService.createPlayerVehicleStatisticsSnapshots(accountIds, vehicleIds);

        verify(wotPlayerVehiclesFeignClient, times(1)).getPlayerVehicleStatistics("", 1, "", "", "", null, "", vehiclesIdsArray);
        verify(vehicleStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndVehicleId(1, 1, "all");
        verify(vehicleStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndVehicleId(1, 2, "all");
        verify(vehicleStatisticsSnapshotsRepository, times(1)).findHighestTotalBattlesByAccountIdAndVehicleId(1, 3, "all");
        verify(expectedStatisticsRepository, times(1)).findById(1);
        verify(expectedStatisticsRepository, times(1)).findById(2);
        verify(expectedStatisticsRepository, times(1)).findById(3);


        // ToDo: Validate Values
    }

    @Test
    public void getPlayerVehicleStatisticsSnapshotsTest() {

    }

}