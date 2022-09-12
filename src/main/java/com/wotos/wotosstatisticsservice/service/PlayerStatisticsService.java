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

import java.time.Instant;
import java.time.LocalDate;
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

    public Map<Integer, Map<String, Float>> getPlayerRecentAverageWn8Map(List<Integer> accountIds, List<String> gameModes, Long timestamp) {
        Map<Integer, Map<String, Float>> playerRecentAverageWn8MapByAccountIdAndGameMode = new HashMap<>();

        for (Integer accountId : accountIds) {
            Map<String, Float> playerRecentAverageWn8MapByGameMode = new HashMap<>();

            for (String gameMode : gameModes) {
                Float recentAverageWn8 = vehicleStatisticsSnapshotsRepository.averageRecentAverageWn8ByGameModeAndAccountId(accountId, gameMode, timestamp).orElse(0f);

                playerRecentAverageWn8MapByGameMode.put(gameMode, recentAverageWn8);
            }

            playerRecentAverageWn8MapByAccountIdAndGameMode.put(accountId, playerRecentAverageWn8MapByGameMode);
        }

        return playerRecentAverageWn8MapByAccountIdAndGameMode;
    }

    public Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> getPlayerStatisticsSnapshotsMap(List<Integer> accountIds, List<String> gameModes) {
        Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> playerStatisticsSnapshotsMapByAccountIdsAndGameMode = new HashMap<>();

        for (Integer accountId : accountIds) {
            Map<String, List<PlayerStatisticsSnapshot>> playerStatisticsSnapshotsMapByGameMode = new HashMap<>();

            for (String gameMode: gameModes) {
                List<PlayerStatisticsSnapshot> playerStatisticsSnapshots = playerStatisticsSnapshotsRepository.findByAccountIdAndGameMode(
                        accountId, gameMode
                ).orElse(new ArrayList<>());

                playerStatisticsSnapshotsMapByGameMode.put(gameMode, playerStatisticsSnapshots);
            }

            playerStatisticsSnapshotsMapByAccountIdsAndGameMode.put(accountId, playerStatisticsSnapshotsMapByGameMode);
        }

        return playerStatisticsSnapshotsMapByAccountIdsAndGameMode;
    }

    public void createPlayerStatisticsSnapshotsByAccountIds(List<Integer> accountIds) {
        for (Integer accountId : accountIds) {
            WotPlayerDetails wotPlayerDetails = fetchWotPlayerDetails(accountId);
            Map<String, WotStatisticsByGameMode> wotStatisticsByGameModeMap = buildWotStatisticsByGameModeMap(wotPlayerDetails.getStatistics());

            wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
                Integer maxBattles = playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(accountId, gameMode).orElse(0);
                Float totalAverageWn8 = vehicleStatisticsSnapshotsRepository.averageAverageWn8ByGameModeAndAccountId(accountId, gameMode).orElse(0f);

                if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
                    playerStatisticsSnapshotsRepository.save(calculatePlayerStatisticsSnapshot(accountId, gameMode, totalAverageWn8, wotStatisticsByGameMode));
                }
            });
        }
    }

    private static Map<String, WotStatisticsByGameMode> buildWotStatisticsByGameModeMap(WotPlayerStatistics wotPlayerStatistics) {
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
        playerStatisticsSnapshot.setCreateTimestamp(Instant.now().getEpochSecond());
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

    private WotPlayerDetails fetchWotPlayerDetails(
            Integer accountId
    ) {
        try {
            return Objects.requireNonNull(
                    wotAccountsFeignClient.getPlayerDetails(APP_ID, "", "", "", "", accountId).getBody()
            ).getData().get(accountId);
        } catch (NullPointerException e) {
            System.out.println("Couldn't fetch WotPlayerDetails with accountId: " + accountId + "\n" + e.getStackTrace());
            return null;
        }
    }

}
