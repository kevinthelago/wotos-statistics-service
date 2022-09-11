package com.wotos.wotosstatisticsservice.service;

import com.sun.istack.NotNull;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.XvmExpectedStatisticsFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotPlayerStatistics;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatisticsByGameMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerStatisticsService {

    @Value("${env.app_id}")
    private String APP_ID;
    @Value("${env.snapshot_rate}")
    private Integer SNAPSHOT_RATE;

    private final WotAccountsFeignClient wotAccountsFeignClient;

    private final VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    private final PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository;

    public PlayerStatisticsService(
            WotAccountsFeignClient wotAccountsFeignClient,

            VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository,
            PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository
    ) {
        this.wotAccountsFeignClient = wotAccountsFeignClient;

        this.vehicleStatisticsSnapshotsRepository = vehicleStatisticsSnapshotsRepository;
        this.playerStatisticsSnapshotsRepository = playerStatisticsSnapshotsRepository;
    }

    // ToDo: Simplify
    public ResponseEntity<Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>>> getPlayerStatisticsSnapshots(List<Integer> accountIds) {
        // ToDo: Calculate recent wn8
        Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> playerStatisticsSnapshotsMapByGameMode = new HashMap<>();

        for (Integer accountId : accountIds) {
            WotPlayerDetails wotPlayerDetails = fetchPlayerDetails(accountId);
            Map<String, WotStatisticsByGameMode> wotStatisticsByGameModeMap = generatePlayerStatisticsByGameModeMap(wotPlayerDetails.getStatistics());
            Map<String, List<PlayerStatisticsSnapshot>> playerStatisticsByGameModeMap = new HashMap<>();

            // ToDo: Maybe implement initial save for players with battles < SNAPSHOT_RATE
            // ToDo: Figure out the discrepancy between player game mode map and vehicle game mode map.
            wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
                Integer maxBattles = playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(accountId, gameMode).orElse(0);
                Float totalAverageWn8 = vehicleStatisticsSnapshotsRepository.averageAverageWn8ByGameModeAndAccountId(accountId, gameMode).orElse(0f);

                if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
                    playerStatisticsSnapshotsRepository.save(calculatePlayerStatisticsSnapshot(accountId, gameMode, totalAverageWn8, wotStatisticsByGameMode));
                }

                List<PlayerStatisticsSnapshot> playerStatisticsSnapshots = playerStatisticsSnapshotsRepository.findByAccountIdAndGameMode(
                        accountId, gameMode
                ).orElse(new ArrayList<>());

                playerStatisticsByGameModeMap.put(gameMode, playerStatisticsSnapshots);
            });

            playerStatisticsSnapshotsMapByGameMode.put(accountId, playerStatisticsByGameModeMap);
        }

        return new ResponseEntity<>(playerStatisticsSnapshotsMapByGameMode, HttpStatus.OK);
    }

    private static Map<String, WotStatisticsByGameMode> generatePlayerStatisticsByGameModeMap(WotPlayerStatistics wotPlayerStatistics) {
        Map<String, WotStatisticsByGameMode> vehicleStatisticsByGameModeMap = new HashMap<>();

        vehicleStatisticsByGameModeMap.put("regular_team", wotPlayerStatistics.getRegularTeam());
        vehicleStatisticsByGameModeMap.put("stronghold_skirmish", wotPlayerStatistics.getStrongholdSkirmish());
        vehicleStatisticsByGameModeMap.put("stronghold_defense", wotPlayerStatistics.getStrongholdDefense());
        vehicleStatisticsByGameModeMap.put("clan", wotPlayerStatistics.getClan());
        vehicleStatisticsByGameModeMap.put("all", wotPlayerStatistics.getAll());
        vehicleStatisticsByGameModeMap.put("company", wotPlayerStatistics.getCompany());
//        vehicleStatisticsByGameModeMap.put("historical", wotPlayerStatistics.getHistorical()); // ToDo: Figure this out.
//        vehicleStatisticsByGameModeMap.put("global_map", wotPlayerStatistics.getGlobalMap()); // ToDo: Figure this out.
        vehicleStatisticsByGameModeMap.put("team", wotPlayerStatistics.getTeam());

        return vehicleStatisticsByGameModeMap;
    }

    private static PlayerStatisticsSnapshot calculatePlayerStatisticsSnapshot(
            @NotNull Integer accountId, @NotNull String gameMode, @NotNull Float totalAverageWn8,
            @NotNull WotStatisticsByGameMode wotStatisticsByGameMode
    ) {
        float wins = wotStatisticsByGameMode.getWins();
        float battles = wotStatisticsByGameMode.getBattles();
        float survivedBattles = wotStatisticsByGameMode.getSurvivedBattles();
        float frags = wotStatisticsByGameMode.getFrags();
        float spotted = wotStatisticsByGameMode.getSpotted();
        float damage = wotStatisticsByGameMode.getDamageDealt();
        float damageTaken = wotStatisticsByGameMode.getDamageReceived();
        float dropperCapturePoints = wotStatisticsByGameMode.getDroppedCapturePoints();
        float xp = wotStatisticsByGameMode.getXp();
        float hits = wotStatisticsByGameMode.getHits();
        float shots = wotStatisticsByGameMode.getShots();
        float stunAssistedDamage = wotStatisticsByGameMode.getStunAssistedDamage();
        float capturePoints = wotStatisticsByGameMode.getCapturePoints();

        float winLossRatio = wins / battles;
        float deaths = battles - survivedBattles == 0 ? 1 : battles - survivedBattles;
        float hitMissRatio = shots > 0 ? hits / shots : 0;
        float killDeathRatio = frags / deaths;
        float averageKillsPerGame = frags / battles;
        float averageSpottingPerGame = spotted / battles;
        float averageDamagePerGame = damage / battles;
        float averageExperiencePerGame = xp / battles;
        float averageDamageReceivedPerGame = damageTaken / battles;
        float averageShotsPerGame = shots / battles;
        float averageStunAssistedDamage = stunAssistedDamage / battles;
        float averageCapturePointsPerGame = capturePoints / battles;
        float averageDroppedCapturePoints = dropperCapturePoints / battles;

        return buildPlayerStatisticsSnapshot(
                accountId, gameMode, (int) battles, (int) survivedBattles, killDeathRatio, hitMissRatio,
                winLossRatio, totalAverageWn8, averageExperiencePerGame, averageDamagePerGame,
                averageKillsPerGame, averageDamageReceivedPerGame, averageShotsPerGame,
                averageStunAssistedDamage, averageCapturePointsPerGame,
                averageDroppedCapturePoints, averageSpottingPerGame
        );
    }

    private static PlayerStatisticsSnapshot buildPlayerStatisticsSnapshot(
            Integer accountId, String gameMode, Integer totalBattles, Integer survivedBattles, Float killDeathRatio,
            Float hitMissRatio, Float winLossRatio, Float totalAverageWn8,
            Float averageExperience, Float averageDamage, Float averageKills,
            Float averageDamageReceived, Float averageShots,
            Float averageStunAssistedDamage, Float averageCapturePoints,
            Float averageDroppedCapturePoints, Float averageSpottingPerGame
    ) {
        PlayerStatisticsSnapshot playerStatisticsSnapshot = new PlayerStatisticsSnapshot();

        playerStatisticsSnapshot.setAccountId(accountId);
        playerStatisticsSnapshot.setGameMode(gameMode);
        playerStatisticsSnapshot.setCreateDate(new Date());
        playerStatisticsSnapshot.setTotalBattles(totalBattles);
        playerStatisticsSnapshot.setSurvivedBattles(survivedBattles);
        playerStatisticsSnapshot.setKillDeathRatio(killDeathRatio);
        playerStatisticsSnapshot.setHitMissRatio(hitMissRatio);
        playerStatisticsSnapshot.setWinLossRatio(winLossRatio);
        playerStatisticsSnapshot.setTotalAverageWn8(totalAverageWn8);
        playerStatisticsSnapshot.setAverageExperience(averageExperience);
        playerStatisticsSnapshot.setAverageDamage(averageDamage);
        playerStatisticsSnapshot.setAverageKills(averageKills);
        playerStatisticsSnapshot.setAverageDamageReceived(averageDamageReceived);
        playerStatisticsSnapshot.setAverageShots(averageShots);
        playerStatisticsSnapshot.setAverageStunAssistedDamage(averageStunAssistedDamage);
        playerStatisticsSnapshot.setAverageCapturePoints(averageCapturePoints);
        playerStatisticsSnapshot.setAverageDroppedCapturePoints(averageDroppedCapturePoints);
        playerStatisticsSnapshot.setAverageSpotting(averageSpottingPerGame);

        return playerStatisticsSnapshot;
    }

    private WotPlayerDetails fetchPlayerDetails(
            Integer accountId
    ) {
        return Objects.requireNonNull(
                wotAccountsFeignClient.getPlayerDetails(APP_ID, "", "", "", "", accountId).getBody()
        ).getData().get(accountId);
    }

}
