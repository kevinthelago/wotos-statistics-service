package com.wotos.wotosstatisticsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.dto.TankStatistics;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.StatisticsSnapshotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class StatisticsService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${env.snapshot_rate}")
    private Integer SNAPSHOT_RATE;
    @Value("${urls.expected_statistics}")
    private String EXPECTED_STATISTICS_URL;
    @Value("${urls.wot_tank_statistics}")
    private String WOT_TANK_STATISTICS_URL;

    private final StatisticsSnapshotsRepository statisticsSnapshotsRepository;
    private final ExpectedStatisticsRepository expectedStatisticsRepository;

    public StatisticsService(
            StatisticsSnapshotsRepository statisticsSnapshotsRepository,
            ExpectedStatisticsRepository expectedStatisticsRepository
            ) {
        this.statisticsSnapshotsRepository = statisticsSnapshotsRepository;
        this.expectedStatisticsRepository = expectedStatisticsRepository;
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true
        );
    }

    public List<StatisticsSnapshot> getPlayerTankStatistics(int playerId, int tankId) {
        TankStatistics tankStatistics = fetchTankStatisticsByPlayerAndTankID(playerId, tankId);
        Optional<List<StatisticsSnapshot>> statisticsSnapshots = statisticsSnapshotsRepository.findByPlayerIdAndTankId(playerId, tankId);

        if (statisticsSnapshots.isPresent()) {
            if (tankStatistics.getAll().getBattles() - statisticsSnapshots.get().get(0).getTotalBattles() >= SNAPSHOT_RATE) {
                List<StatisticsSnapshot> newList = statisticsSnapshots.get();
                StatisticsSnapshot newSnapshot = calculateTankStatisticsSnapshot(playerId, tankStatistics);

                statisticsSnapshotsRepository.save(newSnapshot);
                return newList;
            } else {
                return statisticsSnapshots.get();
            }
        } else {
            List<StatisticsSnapshot> newSnapshots = statisticsSnapshots.get();
            StatisticsSnapshot newSnapshot = calculateTankStatisticsSnapshot(playerId, tankStatistics);
            newSnapshots.add(newSnapshot);
            statisticsSnapshotsRepository.save(newSnapshot);

            return newSnapshots;
        }
    }

    /*
    * ToDo: As of now this method is returning the freshly calculated snapshot rather than snapshot snapshot.
    * */
    public List<StatisticsSnapshot> getAllPlayerTankStatistics(int playerId) {
        List<TankStatistics> tanksStatistics = fetchAllTankStatisticsByPlayerID(playerId);
        List<StatisticsSnapshot> statisticsSnapshots = new ArrayList<>();

        tanksStatistics.forEach(tankStatistics -> {
            Optional<List<StatisticsSnapshot>> statisticsSnapshotsFromDb = statisticsSnapshotsRepository.findByPlayerIdAndTankId(playerId, tankStatistics.getTankId());
            StatisticsSnapshot statisticsSnapshot = calculateTankStatisticsSnapshot(playerId, tankStatistics);
            statisticsSnapshots.add(statisticsSnapshot);

            if (statisticsSnapshotsFromDb.isPresent()) {
                if (tankStatistics.getAll().getBattles() - statisticsSnapshotsFromDb.get().get(0).getTotalBattles() >= SNAPSHOT_RATE) {
                    statisticsSnapshotsRepository.save(statisticsSnapshot);
                }
            } else {
                statisticsSnapshotsRepository.save(statisticsSnapshot);
            }
        });

        return statisticsSnapshots;
    }

    private List<StatisticsSnapshot> calculateAllTankStatisticsByPlayerID(int playerID) {
        List<TankStatistics> tanksStatistics = fetchAllTankStatisticsByPlayerID(playerID);
        List<StatisticsSnapshot> statisticsSnapshots = new ArrayList<>();

        assert tanksStatistics != null;
        tanksStatistics.forEach(tankStatistics ->
            statisticsSnapshots.add(calculateTankStatisticsSnapshot(playerID, tankStatistics))
        );

        return statisticsSnapshots;
    }

    private StatisticsSnapshot calculateTankStatisticsSnapshot(int playerID, @NotNull TankStatistics tankStatistics) {
        ExpectedStatistics expectedStatistics = expectedStatisticsRepository.findById(tankStatistics.getTankId()).get();

        float wins = tankStatistics.getAll().getWins();
        float battles = tankStatistics.getAll().getBattles();
        float survivedBattles = tankStatistics.getAll().getSurvivedBattles();
        float frags = tankStatistics.getAll().getFrags();
        float spotted = tankStatistics.getAll().getSpotted();
        float damage = tankStatistics.getAll().getDamageDealt();
        float defense = tankStatistics.getAll().getDroppedCapturePoints();
        float xp = tankStatistics.getAll().getXp();
        float hits = tankStatistics.getAll().getHits();
        float shots = tankStatistics.getAll().getShots();

        float winRate = wins / battles;
        float deaths = battles - survivedBattles == 0 ? 1 : battles - survivedBattles;
        float killRatio = frags / deaths;
        float hitRatio = shots > 0 ? hits / shots : 0;
        float avgKPG = frags / battles;
        float avgSpot = spotted / battles;
        float avgDPG = damage / battles;
        float avgXP = xp / battles;

        float tune = 10000;

        float DAMAGE = Math.round((avgDPG / expectedStatistics.getExpected_damage()) * tune) / tune;
        float SPOT = Math.round((avgSpot / expectedStatistics.getExpected_spot()) * tune) / tune;
        float FRAG = Math.round((avgKPG / expectedStatistics.getExpected_frag()) * tune) / tune;
        float DEFENSE = Math.round(((defense / battles) / expectedStatistics.getExpected_defense()) * tune) / tune;
        float WIN = Math.round((winRate / expectedStatistics.getExpected_win_rate()) * (tune * 100)) / tune;

        float DAMAGEc = (float) Math.max(0, (DAMAGE - 0.22) / 0.78);
        float SPOTc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (SPOT - 0.38) / 0.62));
        float FRAGc = (float) Math.max(0, Math.min(DAMAGEc + 0.2, (FRAG - 0.12) / 0.88));
        float DEFENSEc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (DEFENSE - 0.10) / 0.9));
        float WINc = (float) Math.max(0, (WIN - 0.71) / 0.29);

        float wn8 = (float) ((980 * DAMAGEc) + (210 * DAMAGEc * FRAGc) + (155 * FRAGc * SPOTc) + (75 * DEFENSEc * FRAGc) + (145 * Math.min(1.8, WINc)));
        int tankID = tankStatistics.getTankId();

        StatisticsSnapshot statisticsSnapShot = buildTankStatisticsSnapshot(
                playerID, tankID, wn8, (int) battles, killRatio, avgXP, hitRatio, avgDPG, avgKPG, winRate
        );

        return statisticsSnapShot;
    }

    private StatisticsSnapshot buildTankStatisticsSnapshot(
            Integer playerID, Integer tankID, Float wn8,
            Integer battles, Float killRatio, Float xp,
            Float hitRatio, Float avgDPG, Float avgKPG, Float winRate
    ) {
        StatisticsSnapshot statisticsSnapShot = new StatisticsSnapshot();
        statisticsSnapShot.setPlayerId(playerID);
        statisticsSnapShot.setTankId(tankID);
//        statisticsSnapShot.setPersonalRating();
        statisticsSnapShot.setAverageWn8(wn8);
        statisticsSnapShot.setAverageExperience(xp);
        statisticsSnapShot.setHitRatio(hitRatio);
        statisticsSnapShot.setKillRatio(killRatio);
        statisticsSnapShot.setWinRate(winRate);
        statisticsSnapShot.setDamagePerGame(avgDPG);
        statisticsSnapShot.setAverageKillsPerGame(avgKPG);
        statisticsSnapShot.setTotalBattles(battles);

        return statisticsSnapShot;
    }

    public List<ExpectedStatistics> fetchExpectedStatistics() {
        String result = restTemplate.getForObject(EXPECTED_STATISTICS_URL, String.class);
        List<ExpectedStatistics> expectedStatistics = new ArrayList<>();

        try {
            JsonNode data = mapper.readTree(result).get("data");

            data.forEach(value -> {
                try {
                    ExpectedStatistics expectedStatistic = mapper.treeToValue(value, ExpectedStatistics.class);
                    expectedStatistics.add(expectedStatistic);
                } catch (JsonProcessingException e) {
                    System.out.println("Error parsing Expected Statistics values: " + e.getMessage());
                }
            });

            return expectedStatistics;
        } catch(IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<TankStatistics> fetchAllTankStatisticsByPlayerID(int playerID) {
        String result = restTemplate.getForObject(WOT_TANK_STATISTICS_URL + "&account_id=" + playerID, String.class);
        List<TankStatistics> tankStats = new ArrayList<>();

        try {
            // Check for each tanks difference of battles more than 100
            JsonNode data = mapper.readTree(result).get("data").get(String.valueOf(playerID));

            data.forEach(value -> {
                try {
                    tankStats.add(mapper.treeToValue(value, TankStatistics.class));
                } catch (JsonProcessingException e) {
                    System.out.println("Error processing tank statistics JSON: " + e.getMessage());
                }
            });

            return tankStats;
        } catch (IOException e) {
            System.out.println("Error parsing fetched player data: " + e.getMessage());
            return null;
        }
    }

    public TankStatistics fetchTankStatisticsByPlayerAndTankID(int playerID, int tankID) {
        String result = restTemplate.getForObject(WOT_TANK_STATISTICS_URL + "&account_id=" + playerID + "&tank_id=" + tankID, String.class);

        try {
            JsonNode data = mapper.readTree(result).get("data").get(String.valueOf(playerID)).get(0);
            TankStatistics tankStats = mapper.treeToValue(data, TankStatistics.class);

            return tankStats;
        } catch (IOException e) {
            System.out.println("Error processing tank statistics with tankID: " + tankID + " and playerID: " + playerID + " " + e.getMessage());
            return null;
        }

    }

}
