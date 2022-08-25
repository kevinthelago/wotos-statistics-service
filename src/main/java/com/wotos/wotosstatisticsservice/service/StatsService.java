package com.wotos.wotosstatisticsservice.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.dto.TankStatistics;
import com.wotos.wotosstatisticsservice.repo.StatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.CalculateStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class StatsService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final Map<String, String> queryParameters = new HashMap<>();

    @Autowired
    private CalculateStatistics calculateStatistics;
    @Autowired
    private WotApiService wotApiService;

    private final StatisticsSnapshotsRepository statisticsSnapshotsRepository;

    public StatsService(
            StatisticsSnapshotsRepository statisticsSnapshotsRepository
            ) {
        this.statisticsSnapshotsRepository = statisticsSnapshotsRepository;
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true
        );
    }

    public List<StatisticsSnapshot> getAllTankStatisticsByPlayerID(int playerId) {
        Optional<List<StatisticsSnapshot>> tankStatisticsListFromDB = statisticsSnapshotsRepository.findAllStatisticsSnapshotsByPlayerId(playerId);
        List<TankStatistics> tanksStatistics = wotApiService.fetchAllTankStatisticsByPlayerID(playerId);

        if (tankStatisticsListFromDB.isPresent()) {
            tanksStatistics.forEach(tankStatistics -> {
                Optional<StatisticsSnapshot> statisticsSnapshot = statisticsSnapshotsRepository.findByPlayerIdAndTankId(playerId, tankStatistics.getTankId());

                if (statisticsSnapshot.isPresent()) {

                }
            });

            return tankStatisticsListFromDB.get();
            // check for updated statistics
        } else {
            List<TankStatistics> tanksStatistics = wotApiService.fetchAllTankStatisticsByPlayerID(playerId);
            List<StatisticsSnapshot> statisticsSnapshots = new ArrayList<>();

            assert tanksStatistics != null;
            tanksStatistics.forEach(tankStatistics ->
                    statisticsSnapshots.add(calculateStatistics.calculateTankStatisticsSnapshot(playerId, tankStatistics))
            );

            return statisticsSnapshots;
        }
    }

    private List<StatisticsSnapshot> calculateAllTankStatisticsByPlayerID(int playerID) {
        List<TankStatistics> tanksStatistics = wotApiService.fetchAllTankStatisticsByPlayerID(playerID);
        List<StatisticsSnapshot> statisticsSnapshots = new ArrayList<>();

        assert tanksStatistics != null;
        tanksStatistics.forEach(tankStatistics ->
            statisticsSnapshots.add(calculateStatistics.calculateTankStatisticsSnapshot(playerID, tankStatistics))
        );

        return statisticsSnapshots;
    }

    /*
    * SINGLE TANK todo
    *              check for updates
    * */

//    public ResponseEntity<TankStatistics> getTankStatisticsByPlayerID(int playerID, int tankID) {
//        Optional<TankStatistics> tankStatisticsFromDB = tankStatisticsRepository.findTankStatisticsByPlayerAndTankID(playerID, tankID);
//
//        if (tankStatisticsFromDB.isPresent()) {
//            // check for updated statistics
//
//            return new ResponseEntity(tankStatisticsFromDB.get(), httpHeaders, HttpStatus.FOUND);
//        } else {
//            TankStatistics tankStatistics = calculateStatistics.calculateTankStatistics(playerID, fetchTankStatisticsByPlayerAndTankID(playerID, tankID));
//
//            return new ResponseEntity(tankStatistics, httpHeaders, HttpStatus.CREATED);
//        }
//    }

    /*
    * PLAYER STATISTICS todo
    *                    check for updates
    *                    create new player stats if not found
    * */

//    public ResponseEntity<PlayerStatistics> getAveragePlayerStatistics(int playerID) {
//        Optional<PlayerStatistics> playerStatistics = playerStatisticsRepository.findById(playerID);
//
//        if (playerStatistics.isPresent()) {
//            // asynchronously update player statistics
//
//            return new ResponseEntity(playerStatistics.get(), httpHeaders, HttpStatus.OK);
//        } else {
//            // create new PlayerStatistics
//
//            return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
//        }
//    }

}
