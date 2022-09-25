package com.wotos.wotosstatisticsservice.service;

import com.sun.istack.NotNull;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.wot.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotPlayerStatistics;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public Map<Integer, Map<String, Float>> getPlayerRecentAverageWn8Map(Integer[] accountIds, String[] gameModes, Long timestamp) {
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

    public Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> getPlayerStatisticsSnapshotsMap(Integer[] accountIds, String[] gameModes) {
        return playerStatisticsSnapshotsRepository.getPlayerStatisticsMap(accountIds, gameModes);
    }

    public Map<Integer, Map<String, PlayerStatisticsSnapshot>> createPlayerStatisticsSnapshotsByAccountIds(Integer[] accountIds) {
        Map<Integer, Map<String, PlayerStatisticsSnapshot>> playerStatisticsSnapshotsMap = new HashMap<>();
        Map<Integer, WotPlayerDetails> wotPlayerDetailsMap = fetchWotPlayerDetails(accountIds);

        for (Integer accountId : accountIds) {
            WotPlayerDetails wotPlayerDetails = wotPlayerDetailsMap.get(accountId);
            Map<String, WotStatistics> wotStatisticsByGameModeMap = buildWotStatisticsByGameModeMap(wotPlayerDetails.getStatistics());
            Map<String, PlayerStatisticsSnapshot> playerStatisticsSnapshotsMapByGameMode = new HashMap<>();

            wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
                if (wotStatisticsByGameMode != null) {
                    Integer maxBattles = playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(accountId, gameMode).orElse(0);
                    Float totalAverageWn8 = vehicleStatisticsSnapshotsRepository.averageAverageWn8ByGameModeAndAccountId(accountId, gameMode).orElse(0f);

                    if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
                        PlayerStatisticsSnapshot playerStatisticsSnapshot = calculatePlayerStatisticsSnapshot(accountId, gameMode, totalAverageWn8, wotStatisticsByGameMode);

                        playerStatisticsSnapshotsMapByGameMode.put(gameMode, playerStatisticsSnapshot);
                        playerStatisticsSnapshotsRepository.save(playerStatisticsSnapshot);
                    }
                }
            });

            playerStatisticsSnapshotsMap.put(accountId, playerStatisticsSnapshotsMapByGameMode);
        }

        return playerStatisticsSnapshotsMap;
    }

    private static Map<String, WotStatistics> buildWotStatisticsByGameModeMap(WotPlayerStatistics wotPlayerStatistics) {
        Map<String, WotStatistics> playerStatisticsByGameModeMap = new HashMap<>();

        playerStatisticsByGameModeMap.put("regular_team", wotPlayerStatistics.getRegularTeam());
        playerStatisticsByGameModeMap.put("stronghold_skirmish", wotPlayerStatistics.getStrongholdSkirmish());
        playerStatisticsByGameModeMap.put("stronghold_defense", wotPlayerStatistics.getStrongholdDefense());
        playerStatisticsByGameModeMap.put("clan", wotPlayerStatistics.getClan());
        playerStatisticsByGameModeMap.put("all", wotPlayerStatistics.getAll());
        playerStatisticsByGameModeMap.put("company", wotPlayerStatistics.getCompany());
        playerStatisticsByGameModeMap.put("team", wotPlayerStatistics.getTeam());
        playerStatisticsByGameModeMap.put("epic", wotPlayerStatistics.getEpic());
        playerStatisticsByGameModeMap.put("fallout", wotPlayerStatistics.getFallout());
        playerStatisticsByGameModeMap.put("random", wotPlayerStatistics.getRandom());
        playerStatisticsByGameModeMap.put("ranked_battles", wotPlayerStatistics.getRankedBattles());

        return playerStatisticsByGameModeMap;
    }

    private static PlayerStatisticsSnapshot calculatePlayerStatisticsSnapshot(
            @NotNull Integer accountId, @NotNull String gameMode, @NotNull Float totalAverageWn8,
            @NotNull WotStatistics wotStatistics
    ) {
        float wins = wotStatistics.getWins();
        float battles = wotStatistics.getBattles();
        float survivedBattles = wotStatistics.getSurvivedBattles();
        float frags = wotStatistics.getFrags();
        float spotted = wotStatistics.getSpotted();
        float damage = wotStatistics.getDamageDealt();
        float damageTaken = wotStatistics.getDamageReceived();
        float dropperCapturePoints = wotStatistics.getDroppedCapturePoints();
        float xp = wotStatistics.getXp();
        float hits = wotStatistics.getHits();
        float shots = wotStatistics.getShots();
        float stunAssistedDamage = wotStatistics.getStunAssistedDamage();
        float capturePoints = wotStatistics.getCapturePoints();

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

    private Map<Integer, WotPlayerDetails> fetchWotPlayerDetails(
            Integer[] accountIds
    ) {
        String[] extras = {"statistics.epic","statistics.fallout","statistics.random","statistics.ranked_battles"};
        String[] fields = {"statistics"};

        try {
            return Objects.requireNonNull(
                    wotAccountsFeignClient.getPlayerDetails(APP_ID, "", extras, fields, "en", accountIds).getBody()
            ).getData();
        } catch (NullPointerException e) {
            System.out.println("Couldn't fetch WotPlayerDetails with accountId: " + accountIds + "\n" + e.getStackTrace());
            return null;
        }
    }

}
